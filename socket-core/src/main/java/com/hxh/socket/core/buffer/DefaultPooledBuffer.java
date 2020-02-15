package com.hxh.socket.core.buffer;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/3 11:17
 */
public class DefaultPooledBuffer implements PooledByteBuffer {

    private DefaultByteBufferPool pool;

    private ByteBuffer byteBuffer;

    private volatile int referenceCount = 1;
    private static final AtomicIntegerFieldUpdater<DefaultPooledBuffer> REFERENCE_COUNT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultPooledBuffer.class, "referenceCount");


    public DefaultPooledBuffer(DefaultByteBufferPool pool, ByteBuffer byteBuffer) {
        this.pool = pool;
        this.byteBuffer = byteBuffer;
    }

    @Override
    public ByteBuffer getBuffer() {
        return byteBuffer;
    }

    @Override
    public void close() {
        if (REFERENCE_COUNT_UPDATER.compareAndSet(this, 1, 0)) {
            pool.deallocate(byteBuffer);
            this.byteBuffer = null;
        }
    }

    @Override
    public boolean isOpen() {
        return referenceCount > 0;
    }

    @Override
    public Buffer limit(int newLimit) {
        return byteBuffer.limit(newLimit);
    }

    @Override
    public Buffer flip() {
        return byteBuffer.flip();
    }

    @Override
    public boolean hasRemaining() {
        return byteBuffer.hasRemaining();
    }

    @Override
    public Buffer clear() {
        return byteBuffer.clear();
    }

    @Override
    public int remaining() {
        return byteBuffer.remaining();
    }

    @Override
    public ByteBuffer put(byte[] src) {
        return byteBuffer.put(src);
    }

    @Override
    public ByteBuffer order(ByteOrder bo) {
        return byteBuffer.order(bo);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pool", pool)
                .append("byteBuffer", byteBuffer)
                .append("referenceCount", referenceCount)
                .toString();
    }
}
