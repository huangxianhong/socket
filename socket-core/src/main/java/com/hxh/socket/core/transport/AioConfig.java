package com.hxh.socket.core.transport;


import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.NamedThreadFactory;
import com.hxh.socket.core.buffer.ByteBufferPool;
import com.hxh.socket.core.buffer.DefaultByteBufferPool;
import com.hxh.socket.core.processor.Processor;
import com.hxh.socket.core.protocol.Protocol;
import com.hxh.socket.core.utils.Time;

import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 13:40
 */
public class AioConfig<T extends IFrame, R extends IFrame> {

    private Processor<T, R> processor;

    private String host = "0.0.0.0";

    private int port = 6666;

    private Protocol<T, R> protocol;

    private Mode mode;

    private long maxTimeToBlockMs = 1000;

    private int bufferSize = 1024;

    private int semaphorePermits = 10000;

    private int semaphoreTimeout = 1000;

    /**
     * 握手超时时间（默认30S）
     */
    private long handshakeTimeout = 30;

    /**
     * 响应超时时间（默认3S,单位MS）
     */
    private long responseTimeout = 3000;

    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    private ThreadPoolExecutor sessionAsyncExecutor = new ThreadPoolExecutor(32, 32, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), new NamedThreadFactory("AIO-SESSION-POOL"), new ThreadPoolExecutor.CallerRunsPolicy());

    private ByteBufferPool byteBufferPool = new DefaultByteBufferPool(1024 * 1024 * 1024, 1024, Time.SYSTEM);

    public void setProtocol(Protocol<T, R> protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol<T, R> getProtocol() {
        return protocol;
    }

    public Processor<T, R> getProcessor() {
        return processor;
    }

    public void setProcessor(Processor<T, R> processor) {
        this.processor = processor;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public ThreadPoolExecutor getSessionAsyncExecutor() {
        return sessionAsyncExecutor;
    }

    public void setSessionAsyncExecutor(ThreadPoolExecutor sessionAsyncExecutor) {
        this.sessionAsyncExecutor = sessionAsyncExecutor;
    }

    public ByteBufferPool getByteBufferPool() {
        return byteBufferPool;
    }

    public void setByteBufferPool(ByteBufferPool byteBufferPool) {
        this.byteBufferPool = byteBufferPool;
    }

    public long getMaxTimeToBlockMs() {
        return maxTimeToBlockMs;
    }

    public void setMaxTimeToBlockMs(long maxTimeToBlockMs) {
        this.maxTimeToBlockMs = maxTimeToBlockMs;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getSemaphorePermits() {
        return semaphorePermits;
    }

    public void setSemaphorePermits(int semaphorePermits) {
        this.semaphorePermits = semaphorePermits;
    }

    public int getSemaphoreTimeout() {
        return semaphoreTimeout;
    }

    public void setSemaphoreTimeout(int semaphoreTimeout) {
        this.semaphoreTimeout = semaphoreTimeout;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public long getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(long handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public long getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(long responseTimeout) {
        this.responseTimeout = responseTimeout;
    }
}
