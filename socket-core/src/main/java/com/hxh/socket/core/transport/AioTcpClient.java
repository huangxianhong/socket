package com.hxh.socket.core.transport;

import com.hxh.socket.core.Client;
import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.NamedThreadFactory;
import com.hxh.socket.core.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 11:13
 */
public class AioTcpClient<T extends IFrame, R extends IFrame> extends AbstractRemote implements Client {

    private static final Logger logger = LoggerFactory.getLogger(AioTcpClient.class);

    protected TcpAioSession<T, R> session;

    protected AioClientConfig config;

    protected AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler();

    protected ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler();

    protected WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler();

    private ReentrantLock lock = new ReentrantLock();

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AtomicBoolean established = new AtomicBoolean(false);


    public AioTcpClient(AioClientConfig config) {
        int max = 65535;
        if (config.getPort() <= 0 || config.getPort() > max) {
            throw new IllegalArgumentException("port must between 0-65535");
        }

        if (config.getProcessor() == null) {
            throw new IllegalArgumentException("processor must not null");
        }

        if (config.getProtocol() == null) {
            throw new IllegalArgumentException("protocol must not null");
        }

        this.config = config;
    }

    @Override
    public TcpAioSession<T, R> start0() {
        start();
        return this.session;
    }

    @Override
    public void start() {
        lock.lock();

        try {
            if (isRunning.get()) {
                return;
            }
            run0();
            isRunning.set(true);
            logger.info("Client started");
        } catch (Exception e) {
            logger.error("Client initialize error", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 创建session
     *
     * @return session
     * @throws Exception
     */
    private TcpAioSession<T, R> run0() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("AIO-CLIENT-POOL"));
        AsynchronousChannelGroup asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(asyncChannelGroup);
        InetSocketAddress serverAddress = new InetSocketAddress(config.getHost(), config.getPort());
        socketChannel.connect(serverAddress, this, acceptCompletionHandler);
        this.waiteEstablish();
        this.scanResponseTable();
        return createSession(socketChannel);
    }

    /**
     * 创建session
     * @param socketChannel
     * @return
     * @throws Exception
     */
    protected TcpAioSession<T,R> createSession(AsynchronousSocketChannel socketChannel) throws Exception {
        TcpAioSession tcpAioSession = new TcpAioSession(socketChannel, readCompletionHandler, writeCompletionHandler, config, this);
        tcpAioSession.start();
        this.session = tcpAioSession;
        return session;
    }

    @Override
    public void stop() {
        logger.info("stop");
        session.stop(false);
    }

    @Override
    void accepted(AsynchronousSocketChannel channel) {
        if (established.compareAndSet(false, true)) {
            logger.info("connected to server {}:{}", config.getHost(), config.getPort());
        }
    }

    /**
     * 等待socket准备完毕
     */
    private void waiteEstablish() {
        while (!established.get()) {
            Time.SYSTEM.sleep(2);
        }
    }
}
