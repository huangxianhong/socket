package com.hxh.socket.core.ssl;

import com.hxh.socket.core.OneWayType;
import com.hxh.socket.core.transport.*;
import com.hxh.socket.frame.AbstractFrame;
import com.hxh.socket.frame.TextFrame;
import com.hxh.socket.frame.processor.DefaultProcessor;
import com.hxh.socket.frame.protocol.FrameProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 16:06
 */
public class SslAioTcpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(SslAioTcpClientTest.class);

    public static void main(String[] args) {
        test6();
    }


    public static void test6() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();
        for (int j = 1; j <= 20; j++) {
            final int jj = j;
            new Thread(() -> {
                for (int i = 1; i <= 500; i++) {
                    TextFrame frame = new TextFrame();
                    frame.setData("data-" + jj + "-" + i);
                    Map<String, String> map = new HashMap<>();
                    map.put("userName", "hxh");
                    frame.setHeader(map);
                    frame.setOneWayType(OneWayType.NO);
                    try {
                        AbstractFrame response = session.sendSync(frame);
                        logger.debug("response:{}", response);
                    } catch (Exception e) {
                        logger.error("同步发送异常",e);
                    }
                }
            }).start();
        }
    }


    public static void test5() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();

        for (int i = 0; i < 10000; i++) {
            TextFrame frame = new TextFrame();
            frame.setData("data-" + i);
            Map<String, String> map = new HashMap<>();
            map.put("userName", "hxh");
            frame.setHeader(map);
            frame.setOneWayType(OneWayType.NO);
            try {
                session.sendOneWay(frame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void test4() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();

        for (int i = 0; i < 10000; i++) {
            TextFrame frame = new TextFrame();
            frame.setData("data-" + i);
            Map<String, String> map = new HashMap<>();
            map.put("userName", "hxh");
            frame.setHeader(map);
            frame.setOneWayType(OneWayType.NO);
            try {
                session.sendAsync(frame, (e) -> logger.debug("response:{}", e));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void test3() {

        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
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


    public static void test2() {
        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    TextFrame frame = new TextFrame();
                    frame.setData("helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg19999");
                    Map<String, String> map = new HashMap<>();
                    map.put("userName", "hxh");
                    frame.setHeader(map);
                    frame.setOneWayType(OneWayType.NO);
                    try {
                        AbstractFrame response = session.sendSync(frame);
                        logger.debug("response:{}", response);
                    } catch (Exception e) {
                        logger.error("同步发送异常",e);
                    }
                }
            }).start();
        }
    }

    public static void test1() {

        AioClientConfig config = new AioClientConfig();
        config.setHost("127.0.0.1");
        config.setMode(Mode.CLIENT);
        config.setProcessor(new DefaultProcessor());
        config.setProtocol(new FrameProtocol());


        //ssl
        SslConfig sslConfig = new SslConfig();
        sslConfig.setKsKeysFilepath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\client.jks");
        sslConfig.setKsTrustFilePath("D:\\workspace\\study\\socket\\socket-frame\\src\\test\\resources\\trustedCerts.jks");


        AioTcpClient client = new SslAioTcpClient(config, sslConfig);
        TcpAioSession<AbstractFrame, AbstractFrame> session = client.start0();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            TextFrame frame = new TextFrame();
            frame.setData("helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg1helloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddghelloddddddddddddddddddddddddddddddddddddddddddddddddddddg19999");
            Map<String, String> map = new HashMap<>();
            map.put("userName", "hxh");
            frame.setHeader(map);
            frame.setOneWayType(OneWayType.NO);
            try {
                AbstractFrame response = session.sendSync(frame);
                logger.debug("response:{}", response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("time={}", System.currentTimeMillis() - start);
    }
}
