package com.hxh.socket.core.utils;

import java.nio.ByteBuffer;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/2 17:17
 */
public class BufferUtil {
    public static ByteBuffer allocate(int capacity) {
        ByteBuffer buf = ByteBuffer.allocate(capacity);
        return buf;
    }

    public static ByteBuffer allocateDirect(int capacity) {
        ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
        return buf;
    }

    public static ByteBuffer wrap(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }
}
