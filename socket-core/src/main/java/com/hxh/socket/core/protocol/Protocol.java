package com.hxh.socket.core.protocol;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.transport.AbstractAioSession;

import java.nio.ByteBuffer;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/25 9:14
 */
public interface Protocol<T extends IFrame, R extends IFrame> {

    /**
     * 解码
     * @param byteBuffer
     * @param session
     * @return
     */
    T decode(ByteBuffer byteBuffer, AbstractAioSession<T, R> session);


    /**
     * 编码
     * @param session
     * @return
     */
    byte[] encode(AbstractAioSession<T, R> session);
}
