package com.hxh.socket.core.transport;

import com.hxh.socket.core.*;
import com.hxh.socket.core.buffer.PooledByteBuffer;
import com.hxh.socket.core.exception.*;
import com.hxh.socket.core.utils.IOUtils;
import com.hxh.socket.core.utils.Time;
import com.hxh.socket.core.utils.UUIDUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:55
 */
public class TcpAioSession<T extends IFrame, R extends IFrame> extends AbstractAioSession<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(TcpAioSession.class);


    /**
     * sessionId
     */
    private String sessionId = UUIDUtils.getId();

    /**
     * channel
     */
    private AsynchronousSocketChannel socket;

    /**
     * 读
     */
    private ReadCompletionHandler readCompletionHandler;

    /**
     * 写
     */
    private WriteCompletionHandler writeCompletionHandler;

    /**
     * 读缓冲
     */
    protected PooledByteBuffer readBuffer;

    /**
     * 写缓冲
     */
    private PooledByteBuffer writeBuffer;

    /**
     * 配置
     */
    protected AioConfig<T, R> config;

    /**
     * Frame发送队列
     */
    private ConcurrentLinkedQueue<T> frameSendQueue = new ConcurrentLinkedQueue<>();

    /**
     * 是否正在对外发送
     */
    private AtomicBoolean isWriting = new AtomicBoolean(false);

    /**
     * 当前收到的Frame
     */
    private T currentRead;

    /**
     * 当前正在发送的Frame
     */
    private T currentWrite;

    /**
     * 当前Session是否正在跑
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AbstractRemote<T, R> remote;

    /**
     * 异步回调线程池
     */
    private ThreadPoolExecutor sessionAsyncExecutor;

    /**
     * 限制发送量
     */
    private Semaphore semaphore;

    /***
     * 尝试获取信号量的超时时间
     */
    private long semaphoreTimeout;

    /**
     * 会话ID
     *
     * @return 会话ID
     */
    @Override
    public String getId() {
        return sessionId;
    }


    /**
     * @param socket                 通道
     * @param readCompletionHandler  读handler
     * @param writeCompletionHandler 写handler
     * @param config                 配置
     */
    public TcpAioSession(AsynchronousSocketChannel socket, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, AioConfig<T, R> config, AbstractRemote remote) throws Exception {
        this.config = config;
        this.sessionAsyncExecutor = config.getSessionAsyncExecutor();
        super.setMode(config.getMode());

        this.socket = socket;
        this.readCompletionHandler = readCompletionHandler;
        this.writeCompletionHandler = writeCompletionHandler;

        this.readBuffer = config.getByteBufferPool().allocate(config.getBufferSize(), config.getMaxTimeToBlockMs());
        this.readBuffer.order(config.getByteOrder());
        this.writeBuffer = config.getByteBufferPool().allocate(config.getBufferSize(), config.getMaxTimeToBlockMs());
        this.writeBuffer.order(config.getByteOrder());
        this.readBuffer.limit(0);
        this.writeBuffer.limit(0);
        this.semaphore = new Semaphore(config.getSemaphorePermits());
        this.semaphoreTimeout = config.getSemaphoreTimeout();
        this.remote = remote;
    }


    /**
     * session开始工作
     */
    @Override
    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }
        this.reading();
        if (config.getProcessor() != null) {
            config.getProcessor().started(this);
        }
    }


    /**
     * 停止
     *
     * @param immediate 是否立即停止
     */
    @Override
    public void stop(boolean immediate) {
        if (!isRunning.compareAndSet(true, false)) {
            return;
        }

        if (!immediate) {
            //如果不是立即停止，就需要处理内存中未处理完毕的内容
            config.getProcessor().event(State.SESSION_STOPPING, this);
            flush();
        }

        IOUtils.close(socket);

        clearBuffer();

        config.getProcessor().event(State.SESSION_STOPPED, this);
    }


    /**
     * 读回调
     *
     * @param i 字节数
     */
    void read(int i) {
        if (checkIoResultFailure(i, readBuffer, Rw.READ)) {
            return;
        }

        readBuffer.limit(i);
        readBuffer.flip();

        try {
            readFrame();
        } catch (Exception e) {
            logger.error("call stopping begin,because readFrame exception occur:", e);
            stop(false);
            return;
        }

        reading();
    }


    /**
     * 将字节流解析成IFrame
     */
    protected void readFrame() throws Exception {
        while (readBuffer != null && readBuffer.hasRemaining()) {
            T current = config.getProtocol().decode(readBuffer.getBuffer(), this);
            if (current == null) {
                break;
            }
            try {
                processWhenReceived(current);
            } catch (Exception e) {
                logger.error("process when received error:", e);
            }
        }
    }


    /**
     * 循环读
     */
    private void reading() {
        if (socket == null) {
            return;
        }

        if (readBuffer == null || !readBuffer.isOpen()) {
            return;
        }

        this.readBuffer.clear();
        if (socket.isOpen()) {
            socket.read(readBuffer.getBuffer(), this, readCompletionHandler);
        }
    }


    /**
     * writeCompletionHandler 回调
     *
     * @param i 字节数
     */
    void written(int i) {
        if (checkIoResultFailure(i, writeBuffer, Rw.WRITE)) {
            return;
        }

        if (currentWrite.remainReading() == 0) {
            config.getProcessor().frameSent(currentWrite, this);
            semaphore.release();
        }

        try {
            writing();
        } catch (Exception e) {
            logger.error("call stopping begin,because writing frame error：", e);
            stop();
        }
    }


    /**
     * 写流到channel
     */
    private void writing() throws Exception {
        if (writeBuffer == null) {
            return;
        }

        if (!writeBuffer.isOpen()) {
            return;
        }

        boolean ready = readFromFrame();
        if (ready) {
            flushToNet(writeBuffer.getBuffer());
        } else {
            if (!isWriting.compareAndSet(true, false)) {
                throw new SendException("isWriting flag is illegal");
            }
        }
    }


    /**
     * 写流到channel
     *
     * @param byteBuffer
     */
    protected void flushToNet(ByteBuffer byteBuffer) throws Exception {
        socket.write(byteBuffer, this, writeCompletionHandler);
    }


    /**
     * 从frame中读取字节
     *
     * @return 字节
     */
    private boolean readFromFrame() {
        if (writeBuffer == null) {
            return false;
        }

        if (writeBuffer.remaining() > 0) {
            return true;
        }
        byte[] bytes = config.getProtocol().encode(this);
        if (bytes == null || bytes.length == 0) {
            return false;
        } else {
            writeBuffer.clear();
            writeBuffer.put(bytes);
            writeBuffer.limit(bytes.length);
            writeBuffer.flip();
            return true;
        }
    }


    /**
     * 检查，并且关闭channel
     *
     * @param i          io操作结果
     * @param byteBuffer 缓冲区
     * @param rw         读/写
     * @return 是否可用
     */
    private boolean checkIoResultFailure(int i, PooledByteBuffer byteBuffer, Rw rw) {
        if (i <= 0) {
            logger.info("call stopping begin[{}]", rw);
            stop(false);
            return true;
        }

        if (byteBuffer == null || !byteBuffer.isOpen()) {
            logger.info("call stopping begin[{}]", rw);
            stop(false);
            return true;
        }

        return false;
    }


    /**
     * 解析到一个完整的IFrame
     *
     * @param current IFrame
     */
    protected void processWhenReceived(T current) {
        FType type = current.getType();

        switch (type) {
            case REQUEST:
                processRequest(current);
                break;
            case RESPONSE:
                processResponse(current);
                break;
            default:
                break;
        }
    }


    /**
     * 处理request请求
     *
     * @param current frame
     */
    private void processRequest(T current) {
        sessionAsyncExecutor.execute(() -> {
            try {
                config.getProcessor().frameReceived(current, this);
            } catch (Exception e) {
                logger.error("process request error:", e);
            }
        });
    }


    /**
     * 处理response请求
     *
     * @param current frame
     */
    private void processResponse(T current) {
        config.getProcessor().frameReceived(current, this);
        ResponseFuture responseFuture = this.get(current.getFrameId());
        if (responseFuture == null) {
            return;
        }
        this.remove(current.getFrameId());

        responseFuture.setResponse(current);

        if (responseFuture.getCallback() == null) {
            return;
        }

        sessionAsyncExecutor.execute(() -> {
            try {
                responseFuture.execute();
            } catch (Exception e) {
                logger.error("process processResponse error:", e);
            }
        });
    }


    /**
     * 获取配置
     *
     * @return 配置
     */
    public AioConfig<T, R> getConfig() {
        return config;
    }


    /**
     * 异步返送
     *
     * @param frame        数据
     * @param sendCallback 异步回调
     */
    @Override
    public void sendAsync(T frame, SendCallback sendCallback) {
        check();

        frame.setOneWayType(OneWayType.NO);
        ResponseFuture<T, R> responseFuture = new ResponseFuture<>(frame.getFrameId(), frame, sendCallback, config.getResponseTimeout());
        this.put(frame.getFrameId(), responseFuture);

        add(frame);

        if (!isWriting.compareAndSet(false, true)) {
            return;
        }

        invoke();
    }


    /**
     * 同步发送
     *
     * @param frame 数据
     * @return 结果
     */
    @Override
    public R sendSync(T frame) {
        check();
        frame.setOneWayType(OneWayType.NO);
        ResponseFuture<T, R> responseFuture = new ResponseFuture<>(frame.getFrameId(), frame, config.getResponseTimeout());
        this.put(frame.getFrameId(), responseFuture);

        add(frame);

        if (isWriting.compareAndSet(false, true)) {
            invoke();
        }

        try {
            if (responseFuture.waitResponse()) {
                return responseFuture.getResponse();
            } else {
                throw new RequestTimeoutException("wait response timeout," + responseFuture);
            }
        } catch (RequestTimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new SendException(e);
        } finally {
            this.remove(frame.getFrameId());
        }
    }


    /**
     * 单向发送
     *
     * @param frame 数据
     */
    @Override
    public void sendOneWay(T frame) {
        check();
        frame.setOneWayType(OneWayType.YES);

        add(frame);

        if (!isWriting.compareAndSet(false, true)) {
            return;
        }

        invoke();
    }


    /**
     * frame发送队列
     *
     * @return 队列
     */
    @Override
    public ConcurrentLinkedQueue<T> getFrameSendQueue() {
        return frameSendQueue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("socket", socket)
                .toString();
    }


    /**
     * 当前收到的Frame
     *
     * @return frame
     */
    @Override
    public T getCurrentRead() {
        return currentRead;
    }


    @Override
    public void setCurrentRead(T currentRead) {
        this.currentRead = currentRead;
    }


    /**
     * 当前正在发送的Frame
     *
     * @return frame
     */
    @Override
    public T getCurrentWrite() {
        return currentWrite;
    }

    @Override
    public void setCurrentWrite(T currentWrite) {
        this.currentWrite = currentWrite;
    }


    /**
     * stop时清除缓冲区
     */
    private void clearBuffer() {
        writeBuffer.close();
        readBuffer.close();
        frameSendQueue.clear();
        writeBuffer = null;
        readBuffer = null;
        currentRead = null;
        currentWrite = null;
    }


    /**
     * 检查是否session是否可用
     */
    private void check() {
        if (!isRunning.get()) {
            throw new SessionException("channel is closed,can't send data");
        }

        boolean acquire;

        try {
            acquire = semaphore.tryAcquire(semaphoreTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SendException(e);
        }

        if (!acquire) {
            throw new SendTooMuchRequestException("send too fast");
        }
    }


    /**
     * 调用send接口时，触发写动作
     */
    private void invoke() {
        try {
            writing();
        } catch (Exception e) {
            logger.error("call stopping begin,because invoke error:", e);
            stop();
            throw new SendException(e);
        }
    }


    /**
     * 当调用{@link TcpAioSession#stop(boolean immediate)},并且immediate=false时执行
     *
     * <ul>
     *     理论上只有当
     *     <li>1.{@link TcpAioSession#readBuffer}</li>
     *     <li>2.{@link TcpAioSession#writeBuffer}</li>
     *     <li>3.{@link TcpAioSession#frameSendQueue}</li>
     *     <li>4.{@link TcpAioSession#currentWrite}</li>
     *     <li>5.{@link TcpAioSession#currentRead}</li>
     *     都处于空时，停止才算合理
     *  </ul>
     */
    private void flush() {
        if (Mode.CLIENT.equals(this.getMode())) {
            processClientMode();
        } else if (Mode.SERVER.equals(this.getMode())) {
            processServerMode();
        }
    }

    /**
     * 如果该session是客户端模式
     */
    private void processClientMode() {
        while (!frameSendQueue.isEmpty() || currentWrite != null) {
            logger.info("{} wait to close the session", config.getMode());
            Time.SYSTEM.sleep(1000);
        }
    }


    /**
     * 如果该session是服务端模式
     */
    private void processServerMode() {
    }


    /**
     * 添加到队列
     *
     * @param frame 帧
     */
    private void add(T frame) {
        try {
            frameSendQueue.add(frame);
        } catch (Exception e) {
            throw new SendException(e);
        }
    }


    /**
     * 向响应列表放数据
     *
     * @param frameId
     * @param responseFuture
     */
    private void put(String frameId, ResponseFuture<T, R> responseFuture) {
        remote.responseTable.put(frameId, responseFuture);
    }


    /**
     * 移除响应等待数据
     *
     * @param frameId
     */
    private void remove(String frameId) {
        remote.responseTable.remove(frameId);
    }

    /**
     * 获取等待数据
     *
     * @param frameId
     * @return
     */
    private ResponseFuture<T, R> get(String frameId) {
        return remote.responseTable.get(frameId);
    }
}
