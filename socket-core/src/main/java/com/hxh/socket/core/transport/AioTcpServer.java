package com.hxh.socket.core.transport;

import com.hxh.socket.core.NamedThreadFactory;
import com.hxh.socket.core.Server;
import com.hxh.socket.core.State;
import com.hxh.socket.core.processor.Processor;
import com.hxh.socket.core.utils.IOUtils;
import com.hxh.socket.core.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
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
public class AioTcpServer extends AbstractRemote implements Server, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AioTcpServer.class);

    /**
     * 线程池
     */
    private ThreadPoolExecutor mainExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1), new NamedThreadFactory("Accept-Main-Thread"), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 是否正在启动
     */
    private AtomicBoolean isStarting = new AtomicBoolean(false);

    /**
     * 启动锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * socket
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
     * 接收连接
     */
    protected AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler();

    /**
     * 读
     */
    protected ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler();

    /**
     * 写
     */
    protected WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler();

    /**
     * 服务端配置
     */
    protected AioServerConfig config;

    /**
     * host
     */
    private String host;

    /**
     * port
     */
    private int port;


    /**
     * 处理器
     */
    private Processor processor;

    private Time time = Time.SYSTEM;

    /**
     * 线程池核心数
     */
    private int corePoolSize;

    /**
     * 线程池最大数
     */
    private int maximumPoolSize;

    /**
     * 队列大小
     */
    private int queueSize;

    /**
     * 线程池核心数
     */
    private int acceptCorePoolSize;

    /**
     * 线程池最大数
     */
    private int acceptMaximumPoolSize;

    /**
     * 接收连接队列大小
     */
    private int acceptQueueSize;


    private AsynchronousChannelGroup asyncChannelGroup;

    private ThreadPoolExecutor executor;

    /**
     * 接收连接线程池
     */
    private ThreadPoolExecutor acceptExecutor;


    /**
     * 构造
     *
     * @param config 配置
     */
    public AioTcpServer(AioServerConfig config) {
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
        this.host = config.getHost();
        this.port = config.getPort();
        this.processor = config.getProcessor();
        this.corePoolSize = config.getCorePoolSize();
        this.maximumPoolSize = config.getMaximumPoolSize();
        this.acceptCorePoolSize = config.getAcceptCorePoolSize();
        this.acceptMaximumPoolSize = config.getAcceptMaximumPoolSize();
        this.queueSize = config.getQueueSize();
        this.acceptQueueSize = config.getAcceptQueueSize();
    }


    /**
     * run
     */
    @Override
    public void run() {
        lock.lock();
        try {
            run0();
        } catch (Exception e) {
            logger.error("Server initialize error", e);
            System.exit(0);
        } finally {
            lock.unlock();
        }
    }

    /**
     * run0
     */
    private void run0() throws Exception {
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueSize), new NamedThreadFactory("AIO-SERVER-POOL"));
        acceptExecutor = new ThreadPoolExecutor(acceptCorePoolSize, acceptMaximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(acceptQueueSize), new NamedThreadFactory("AIO-ACCEPT-POOL"), new ThreadPoolExecutor.CallerRunsPolicy());
        asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        serverSocketChannel = AsynchronousServerSocketChannel.open(asyncChannelGroup);
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        serverSocketChannel.bind(serverAddress, this.config.getBacklog());

        this.scanResponseTable();
        this.accepting();

        if (processor != null) {
            processor.started(this);
        }
        logger.info("Server started and listening on port {}", port);
        while (true) {
            time.sleep(1000);
        }
    }


    /**
     * 启动TcpServer
     */
    @Override
    public void start() {
        if (!isStarting.compareAndSet(false, true)) {
            logger.warn("Server is initialized, cant start twice");
            return;
        }
        mainExecutor.execute(this);
    }


    /**
     * 停止server
     */
    @Override
    public void stop() {
        if (asyncChannelGroup != null) {
            try {
                asyncChannelGroup.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mainExecutor.shutdownNow();
        logger.info("Server stopped");
        System.exit(0);
    }


    /**
     * 接收socket连接
     *
     * @param channel 通道
     */
    @Override
    void accepted(AsynchronousSocketChannel channel) {
        if (channel == null) {
            return;
        }
        acceptExecutor.execute(() -> acceptWork(channel));
        accepting();
    }


    /**
     * 异步创建session
     *
     * @param channel
     */
    private void acceptWork(AsynchronousSocketChannel channel) {
        AbstractAioSession tcpAioSession = null;
        try {
            tcpAioSession = createSession(channel);
        } catch (Exception e) {
            logger.error("create session error:", e);
            IOUtils.close(channel);
        }
        if (processor != null && tcpAioSession != null) {
            this.processor.event(State.NEW_SESSION, tcpAioSession);
        }
    }


    /**
     * 创建session
     *
     * @param channel 通道
     * @return session
     */
    protected AbstractAioSession createSession(AsynchronousSocketChannel channel) throws Exception {
        TcpAioSession tcpAioSession;
        tcpAioSession = new TcpAioSession(channel, readCompletionHandler, writeCompletionHandler, config, this);
        tcpAioSession.start();
        return tcpAioSession;
    }


    /**
     * 接收下一次连接
     */
    private void accepting() {
        serverSocketChannel.accept(this, acceptCompletionHandler);
    }
}
