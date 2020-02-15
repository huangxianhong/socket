package com.hxh.socket.core.processor;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.transport.AbstractAioSession;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/2 10:17
 */
public interface RequestProcessor<T extends IFrame, R extends IFrame> {

    /**
     * 处理request请求
     * @param frame
     * @param session
     * @return
     */
    R processRequest(T frame, AbstractAioSession<T, R> session);
}
