package com.hxh.socket.core.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/3 11:42
 */
public interface ByteBufferPool extends Closeable {

    /**
     * 从池中获取buffer
     * @param size 大小
     * @param maxTimeToBlockMs 等待时间
     * @return buffer
     * @throws InterruptedException e
     */
    PooledByteBuffer allocate(int size, long maxTimeToBlockMs) throws InterruptedException;


    /**
     * close
     * @throws IOException e
     */
    @Override
    void close() throws IOException;
}
