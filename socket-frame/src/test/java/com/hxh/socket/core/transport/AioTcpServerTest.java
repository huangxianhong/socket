package com.hxh.socket.core.transport;

import com.hxh.socket.frame.processor.DefaultProcessor;
import com.hxh.socket.frame.protocol.FrameProtocol;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 14:26
 */
public class AioTcpServerTest {
    public static void main(String[] args) {
        AioServerConfig config = new AioServerConfig();
        DefaultProcessor processor = new DefaultProcessor();
        config.setProcessor(processor);
        config.setProtocol(new FrameProtocol());
        config.setMode(Mode.SERVER);
        try {
            AioTcpServer server = new AioTcpServer(config);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
