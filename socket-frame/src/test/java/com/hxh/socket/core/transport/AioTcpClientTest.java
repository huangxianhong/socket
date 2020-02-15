package com.hxh.socket.core.transport;

import com.hxh.socket.core.OneWayType;
import com.hxh.socket.core.ResponseFuture;
import com.hxh.socket.core.SendCallback;
import com.hxh.socket.frame.AbstractFrame;
import com.hxh.socket.frame.PingFrame;
import com.hxh.socket.frame.TextFrame;
import com.hxh.socket.frame.processor.DefaultProcessor;
import com.hxh.socket.frame.protocol.FrameProtocol;
import com.hxh.socket.core.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/26 14:50
 */
public class AioTcpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(AioTcpClientTest.class);

    public static void main(String[] args) {
        test4();
    }


    public static void test4() {

        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());
        AioTcpClient client = new AioTcpClient(config);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();
        long start = System.currentTimeMillis();
        TextFrame frame = new TextFrame();
        frame.setData("helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg19999");
        Map<String, String> map = new HashMap<>();
        map.put("userName", "hxh");
        frame.setHeader(map);
        frame.setOneWayType(OneWayType.NO);
        AbstractFrame response = session.sendSync(frame);

        logger.debug("response:{}", response);
        logger.info("time={}", System.currentTimeMillis() - start);
    }


    public static void ping(){
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());
        AioTcpClient client = new AioTcpClient(config);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();

        PingFrame frame = new PingFrame();
        frame.setOneWayType(OneWayType.YES);


        session.sendOneWay(frame);
    }


    public static void test3() {

        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());
        AioTcpClient client = new AioTcpClient(config);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            TextFrame frame = new TextFrame();
            frame.setData("hello");
            Map<String, String> map = new HashMap<>();
            map.put("userName", "hxh");
            frame.setHeader(map);
            frame.setOneWayType(OneWayType.YES);
            AbstractFrame response = session.sendSync(frame);

            logger.debug("response:{}", response);
        }
        logger.info("time={}", System.currentTimeMillis() - start);
    }


    public static void test2() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());
        AioTcpClient client = new AioTcpClient(config);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();
        for(int i=0;i<1000;i++) {
            TextFrame frame = new TextFrame();
            frame.setData("hello");
            Map<String, String> map = new HashMap<>();
            map.put("userName", "hxh");
            frame.setHeader(map);
            frame.setOneWayType(OneWayType.YES);
            session.sendAsync(frame, new SendCallback() {
                @Override
                public void invoke(ResponseFuture responseFuture) {
                    logger.info("response={}", responseFuture);
                }
            });
        }



        Time.SYSTEM.sleep(100000);

    }

    public static void test1() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);

        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        AioTcpClient client = new AioTcpClient(config);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();


        for (int j = 0; j < 100; j++) {
            final int t = j;
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    String suffix = t + "-" + i;
                    TextFrame frame = new TextFrame();
                    frame.setData("hello-" + suffix);
                    Map<String, String> map = new HashMap<>();
                    map.put("userName", "hxh-" + suffix);
                    frame.setHeader(map);
                    session.sendOneWay(frame);
                }
            }).start();
        }
    }
}
