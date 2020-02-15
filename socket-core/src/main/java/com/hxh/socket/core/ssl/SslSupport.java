package com.hxh.socket.core.ssl;

import com.hxh.socket.core.NamedThreadFactory;
import com.hxh.socket.core.exception.HandshakeException;
import com.hxh.socket.core.exception.SslException;
import com.hxh.socket.core.utils.BufferUtil;
import com.hxh.socket.core.utils.SslUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.concurrent.*;

/**
 * description:
 * <ul>
 *     参考文档：
 *     <li><a href="https://github.com/alkarn/sslengine.example.git">sslengine.example</a></li>
 *     <li><a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html#SSLEngine">JSSERefGuide</a></li>
 *     <li><a href="https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/network/SslTransportLayer.java">org.apache.kafka.common.network.SslTransportLayer</a></li>
 * </ul>
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 11:28
 */
public class SslSupport {

    private static final Logger logger = LoggerFactory.getLogger(SslSupport.class);

    /**
     * ssl 配置
     */
    private SslConfig config;

    /**
     * ssl 上下文
     */
    private SSLContext context;
    private int corePoolSize = 1;
    private int maximumPoolSize = 1;

    /**
     * 线程池
     */
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), new NamedThreadFactory("AIO-HANDSHAKE-POOL"), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 读/写回调
     */
    private CompletionHandler completionHandler = new CompletionHandler<Integer, SslHandshakeWrapper>() {
        @Override
        public void completed(Integer result, SslHandshakeWrapper attachment) {
            if (result <= 0) {
                attachment.setEof(true);
            }

            synchronized (attachment) {
                callback(attachment);
            }
        }

        @Override
        public void failed(Throwable exc, SslHandshakeWrapper attachment) {
            logger.error("handshake completionHandler error:", exc);
        }
    };


    /**
     * 构造
     *
     * @param sslConfig 配置
     */
    SslSupport(SslConfig sslConfig) {
        this.config = sslConfig;
        try {
            context = SSLContext.getInstance(config.getProtocol());
            KeyManager[] keyManagers = SslUtils.createKeyManagers(config.getKsKeysFilepath(), config.getKsKeystorePassword(), config.getKsKeyPassword());
            TrustManager[] trustManagers = SslUtils.createTrustManagers(config.getKsTrustFilePath(), config.getKsTrustKeystorePassword());
            context.init(keyManagers, trustManagers, new SecureRandom());
        } catch (Exception e) {
            throw new SslException(e);
        }
    }


    public SslConfig getConfig() {
        return config;
    }

    public void setConfig(SslConfig config) {
        this.config = config;
    }

    public SSLContext getContext() {
        return context;
    }

    protected ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getPacketBufferSize());
    }

    protected ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getApplicationBufferSize());
    }

    /**
     * Compares <code>sessionProposedCapacity<code> with buffer's capacity. If buffer's capacity is smaller,
     * returns a buffer with the proposed capacity. If it's equal or larger, returns a buffer
     * with capacity twice the size of the initial one.
     *
     * @param buffer                  - the buffer to be enlarged.
     * @param sessionProposedCapacity - the minimum size of the new buffer, proposed by {@link SSLSession}.
     * @return A new buffer with a larger capacity.
     */
    private ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
        if (sessionProposedCapacity > buffer.capacity()) {
            buffer = BufferUtil.allocate(sessionProposedCapacity);
        } else {
            buffer = BufferUtil.allocate(buffer.capacity() * 2);
        }
        return buffer;
    }

    /**
     * Handles {@link SSLEngineResult.Status#BUFFER_UNDERFLOW}. Will check if the buffer is already filled, and if there is no space problem
     * will return the same buffer, so the client tries to read again. If the buffer is already filled will try to enlarge the buffer either to
     * session's proposed size or to a larger capacity. A buffer underflow can happen only after an unwrap, so the buffer will always be a
     * peerNetData buffer.
     *
     * @param buffer - will always be peerNetData buffer.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @return The same buffer if there is no space problem or a new buffer with the same data but more space.
     * @throws Exception
     */
    protected ByteBuffer handleBufferUnderflow(SSLEngine engine, ByteBuffer buffer) {
        if (engine.getSession().getPacketBufferSize() < buffer.limit()) {
            return buffer;
        } else {
            ByteBuffer replaceBuffer = enlargePacketBuffer(engine, buffer);
            buffer.flip();
            replaceBuffer.put(buffer);
            return replaceBuffer;
        }
    }


    /**
     * 握手准备
     *
     * @param channel
     * @param sslEngine
     * @return
     */
    public SslHandshakeWrapper prepareHandshake(AsynchronousSocketChannel channel, SSLEngine sslEngine) {
        SslHandshakeWrapper wrapper = new SslHandshakeWrapper();
        wrapper.setChannel(channel);
        wrapper.setEngine(sslEngine);

        // Create byte buffers to use for holding application data
        int appBufferSize = sslEngine.getSession().getApplicationBufferSize();
        int packetBufferSize = sslEngine.getSession().getPacketBufferSize();

        ByteBuffer appWriteBuffer = BufferUtil.allocate(appBufferSize);
        ByteBuffer netWriteBuffer = BufferUtil.allocate(packetBufferSize);

        ByteBuffer appReadBuffer = BufferUtil.allocate(appBufferSize);
        ByteBuffer netReadBuffer = BufferUtil.allocate(packetBufferSize);

        wrapper.setAppReadBuffer(appReadBuffer);
        wrapper.setAppWriteBuffer(appWriteBuffer);
        wrapper.setNetWriteBuffer(netWriteBuffer);
        wrapper.setNetReadBuffer(netReadBuffer);
        return wrapper;
    }

    /**
     * 握手
     *
     * @param channel
     * @param engine
     * @throws Exception
     */
    protected void handshake(AsynchronousSocketChannel channel, SSLEngine engine, long timeout) throws Exception {
        SslHandshakeWrapper wrapper = this.prepareHandshake(channel, engine);

        this.startHandshake(wrapper.getChannel(), wrapper.getEngine());
        this.doHandshake(wrapper);
        boolean wait = wrapper.wait(timeout, TimeUnit.SECONDS);

        if (!wait) {
            throw new HandshakeException("handshake timeout");
        }
        if (wrapper.isEof()) {
            throw new HandshakeException("handshake eof");
        }
    }


    /**
     * 开始握手
     *
     * @param channel
     * @param sslEngine
     * @throws Exception
     */
    private void startHandshake(AsynchronousSocketChannel channel, SSLEngine sslEngine) throws Exception {
        sslEngine.beginHandshake();
    }


    /**
     * handler回调
     *
     * @param wrapper
     */
    private void callback(SslHandshakeWrapper wrapper) {
        try {
            doHandshake(wrapper);
        } catch (Exception e) {
            logger.error("doHandshake error:", e);
        }
    }

    /**
     * 握手
     * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html#SSLEngine">参考</a>
     *
     * <ul>
     *     <li>FINISHED: SSLEngine 已经完成握手。</li>
     *     <li>NEED_TASK: SSLEngine 在继续进行握手前需要一个（或多个）代理任务的结果</li>
     *     <li>NEED_UNWRAP: 在继续进行握手前，SSLEngine 需要从远端接收数据</li>
     *     <li>NEED_WRAP: 在继续进行握手前，SSLEngine 必须向远端发送数据，所以应该调用 SSLEngine.wrap()。</li>
     *     <li>NOT_HANDSHAKING :SSLEngine 当前没有进行握手。</li>
     * </ul>
     *
     * @param wrapper
     * @return
     * @throws Exception
     */
    private void doHandshake(SslHandshakeWrapper wrapper) throws Exception {
        ByteBuffer appWriteBuffer = wrapper.getAppWriteBuffer();
        ByteBuffer netWriteBuffer = wrapper.getNetWriteBuffer();

        ByteBuffer appReadBuffer = wrapper.getAppReadBuffer();
        ByteBuffer netReadBuffer = wrapper.getNetReadBuffer();

        SSLEngineResult res;
        SSLEngineResult.HandshakeStatus hs;

        SSLEngine engine = wrapper.getEngine();

        AsynchronousSocketChannel channel = wrapper.getChannel();

        if (wrapper.isEof()) {
            wrapper.markDone();
            return;
        }

        while (!wrapper.isEnd()) {
            hs = engine.getHandshakeStatus();
            switch (hs) {
                case NEED_UNWRAP:
                    netReadBuffer.flip();

                    //如果有数据，进行unwrap,否则从channel中读取
                    if (netReadBuffer.hasRemaining()) {
                        res = engine.unwrap(netReadBuffer, appReadBuffer);
                        netReadBuffer.compact();
                    } else {
                        netReadBuffer.clear();
                        channel.read(netReadBuffer, wrapper, completionHandler);
                        return;
                    }

                    //看是否握手成功
                    if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                        wrapper.markDone();
                    }

                    switch (res.getStatus()) {
                        case OK:
                            break;
                        case BUFFER_OVERFLOW:
                            // Will occur when appReadBuffer's capacity is smaller than the data derived from netReadBuffer's unwrap.
                            appReadBuffer = enlargeApplicationBuffer(engine, appReadBuffer);
                            wrapper.setAppReadBuffer(appReadBuffer);
                            break;
                        case BUFFER_UNDERFLOW:
                            // Will occur either when no data was read from the peer or when the netReadBuffer buffer was too small to hold all peer's data.
                            netReadBuffer = handleBufferUnderflow(engine, netReadBuffer);
                            wrapper.setNetReadBuffer(netReadBuffer);
                            return;
                        case CLOSED:
                            throw new EOFException("SSL handshake status CLOSED during handshake UNWRAP");
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + res.getStatus());
                    }
                    break;
                case NEED_WRAP:
                    netWriteBuffer.clear();
                    res = engine.wrap(appWriteBuffer, netWriteBuffer);

                    switch (res.getStatus()) {
                        case OK:
                            if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                                wrapper.markDone();
                            }
                            netWriteBuffer.flip();
                            channel.write(netWriteBuffer, wrapper, completionHandler);
                            return;
                        case BUFFER_OVERFLOW:
                            // Will occur if there is not enough space in netWriteBuffer buffer to write all the data that would be generated by the method wrap.
                            // Since netWriteBuffer is set to session's packet size we should not get to this point because SSLEngine is supposed
                            // to produce messages smaller or equal to that, but a general handling would be the following:
                            netWriteBuffer = enlargePacketBuffer(engine, netWriteBuffer);
                            wrapper.setNetWriteBuffer(netWriteBuffer);
                            break;
                        case BUFFER_UNDERFLOW:
                            throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                        case CLOSED:
                            throw new EOFException("SSL handshake status CLOSED during handshake WRAP");
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + res.getStatus());
                    }
                    break;
                case NEED_TASK:
                    runDelegatedTasks(wrapper.getEngine());
                    break;
                case FINISHED:
                    logger.info("FINISHED");
                    break;
                case NOT_HANDSHAKING:
                    logger.info("NOT_HANDSHAKING");
                    break;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + hs);
            }
        }
        logger.debug("handshake end,channel={}", channel);
    }

    /**
     * @param engine
     * @return
     */
    private void runDelegatedTasks(SSLEngine engine) {
        Runnable task;
        while ((task = engine.getDelegatedTask()) != null) {
            executor.execute(task);
        }
    }
}