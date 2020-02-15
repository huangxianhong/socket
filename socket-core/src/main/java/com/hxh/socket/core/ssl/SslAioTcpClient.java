package com.hxh.socket.core.ssl;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.transport.AioClientConfig;
import com.hxh.socket.core.transport.AioTcpClient;
import com.hxh.socket.core.transport.TcpAioSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 16:04
 */
public class SslAioTcpClient<T extends IFrame, R extends IFrame> extends AioTcpClient {

    private static final Logger logger = LoggerFactory.getLogger(SslAioTcpClient.class);

    /**
     * ssl配置
     */
    private SslConfig sslConfig;

    /**
     * ssl支持
     */
    private SslSupport sslSupport;

    public SslAioTcpClient(AioClientConfig config, SslConfig sslConfig) {
        super(config);
        this.sslConfig = sslConfig;
    }

    @Override
    public void start() {
        sslSupport = new SslSupport(sslConfig);
        super.start();
    }


    @Override
    protected TcpAioSession createSession(AsynchronousSocketChannel socketChannel) throws Exception {
        SSLEngine sslEngine = sslSupport.getContext().createSSLEngine(config.getHost(), config.getPort());
        sslEngine.setUseClientMode(true);

        doHandshake(socketChannel, sslEngine);
        logger.debug("handshake success ,start to create session on client");

        TcpAioSession tcpAioSession = new SslTcpAioSession(socketChannel, readCompletionHandler, writeCompletionHandler, config, sslSupport, sslEngine, this);
        tcpAioSession.start();
        this.session = tcpAioSession;
        return session;
    }


    /**
     * 校验是否握手成功
     *
     * @param channel chanel
     * @param engine  引擎
     * @return 是否成功
     * @throws IOException
     */
    private void doHandshake(AsynchronousSocketChannel channel, SSLEngine engine) throws Exception {
        sslSupport.handshake(channel, engine, config.getHandshakeTimeout());
    }
}
