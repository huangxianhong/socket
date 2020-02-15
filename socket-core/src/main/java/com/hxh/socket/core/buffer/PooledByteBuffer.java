package com.hxh.socket.core.buffer;

import java.io.Closeable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/3 11:16
 */
public interface PooledByteBuffer extends AutoCloseable, Closeable {

    /**
     * 获取buffer
     * @return buffer
     */
    ByteBuffer getBuffer();

    /**
     * close
     */
    @Override
    void close();

    /**
     * 是否打开
     * @return true/false
     */
    boolean isOpen();


    /**
     * limit
     * @param newLimit
     * @return
     */
    Buffer limit(int newLimit);

    /**
     * flip
     * @return
     */
    Buffer flip();

    /**
     * hasRemaining
     * @return
     */
    boolean hasRemaining();

    /**
     * clear
     * @return
     */
    Buffer clear();

    /**
     * remaining
     * @return
     */
    int remaining();


    /**
     * put
     * @param src
     * @return
     */
    ByteBuffer put(byte[] src);


    /**
     * order
     * @param bo
     * @return
     */
    ByteBuffer order(ByteOrder bo);
}
