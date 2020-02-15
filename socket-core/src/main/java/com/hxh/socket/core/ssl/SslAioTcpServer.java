package com.hxh.socket.core.ssl;

import com.hxh.socket.core.transport.AbstractAioSession;
import com.hxh.socket.core.transport.AioServerConfig;
import com.hxh.socket.core.transport.AioTcpServer;
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
 * @date Created in 2020/2/8 11:58
 */
public class SslAioTcpServer extends AioTcpServer {

    private static final Logger logger = LoggerFactory.getLogger(SslAioTcpServer.class);

    /**
     * ssl配置
     */
    private SslConfig sslConfig;

    /**
     * ssl支持
     */
    private SslSupport sslSupport;

    public SslAioTcpServer(AioServerConfig config, SslConfig sslConfig) {
        super(config);
        this.sslConfig = sslConfig;
    }


    /**
     * 创建带ssl的session
     *
     * @param channel 通道
     * @return
     * @throws Exception
     */
    @Override
    protected AbstractAioSession createSession(AsynchronousSocketChannel channel) throws Exception {

        SSLEngine sslEngine = sslSupport.getContext().createSSLEngine();
        sslEngine.setUseClientMode(false);

        doHandshake(channel, sslEngine);

        logger.debug("handshake success ,start to create session on server,channel={}", channel);

        SslTcpAioSession sslTcpAioSession;
        sslTcpAioSession = new SslTcpAioSession(channel, readCompletionHandler, writeCompletionHandler, config, sslSupport, sslEngine, this);
        sslTcpAioSession.start();
        return sslTcpAioSession;
    }

    /**
     * 启动
     */
    @Override
    public void start() {
        sslSupport = new SslSupport(sslConfig);
        super.start();
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
