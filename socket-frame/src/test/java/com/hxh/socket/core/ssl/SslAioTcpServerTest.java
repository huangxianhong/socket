package com.hxh.socket.core.ssl;

import com.hxh.socket.core.transport.AioServerConfig;
import com.hxh.socket.core.transport.AioTcpServer;
import com.hxh.socket.core.transport.Mode;
import com.hxh.socket.frame.processor.DefaultProcessor;
import com.hxh.socket.frame.protocol.FrameProtocol;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 12:13
 */
public class SslAioTcpServerTest {
    public static void main(String[] args) {
        //基础配置
        AioServerConfig config = new AioServerConfig();
        DefaultProcessor processor = new DefaultProcessor();
        config.setProcessor(processor);
        config.setProtocol(new FrameProtocol());
        config.setMode(Mode.SERVER);

        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\server.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        try {
            AioTcpServer server = new SslAioTcpServer(config,sslConfig);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
