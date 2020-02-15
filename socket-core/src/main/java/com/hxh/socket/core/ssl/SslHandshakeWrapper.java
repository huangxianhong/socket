package com.hxh.socket.core.ssl;

import javax.net.ssl.SSLEngine;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/9 10:12
 */
public class SslHandshakeWrapper {
    private AsynchronousSocketChannel channel;
    private SSLEngine engine;
    private boolean eof;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private boolean end;

    private ByteBuffer netReadBuffer;
    private ByteBuffer netWriteBuffer;
    private ByteBuffer appReadBuffer;
    private ByteBuffer appWriteBuffer;

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    public SSLEngine getEngine() {
        return engine;
    }

    public void setEngine(SSLEngine engine) {
        this.engine = engine;
    }

    public boolean isEof() {
        return eof;
    }

    public void setEof(boolean eof) {
        this.eof = eof;
    }

    public ByteBuffer getNetReadBuffer() {
        return netReadBuffer;
    }

    public void setNetReadBuffer(ByteBuffer netReadBuffer) {
        this.netReadBuffer = netReadBuffer;
    }

    public ByteBuffer getNetWriteBuffer() {
        return netWriteBuffer;
    }

    public void setNetWriteBuffer(ByteBuffer netWriteBuffer) {
        this.netWriteBuffer = netWriteBuffer;
    }

    public ByteBuffer getAppReadBuffer() {
        return appReadBuffer;
    }

    public void setAppReadBuffer(ByteBuffer appReadBuffer) {
        this.appReadBuffer = appReadBuffer;
    }

    public ByteBuffer getAppWriteBuffer() {
        return appWriteBuffer;
    }

    public void setAppWriteBuffer(ByteBuffer appWriteBuffer) {
        this.appWriteBuffer = appWriteBuffer;
    }

    public boolean wait(long timeout, TimeUnit unit) throws InterruptedException {
        return countDownLatch.await(timeout, unit);
    }


    public boolean isEnd() {
        return end;
    }


    public void markDone() {
        countDownLatch.countDown();
        end = true;
    }
}
