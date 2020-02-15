package com.hxh.socket.core.processor;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.LifeCycle;
import com.hxh.socket.core.State;
import com.hxh.socket.core.transport.AbstractAioSession;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 13:39
 */
public interface Processor<T extends IFrame, R extends IFrame> {

    /**
     * frame发送完成
     * @param frame
     * @param session
     */
    void frameSent(T frame, AbstractAioSession<T, R> session);


    /**
     * 收到一个完整frame
     * @param frame
     * @param session
     */
    void frameReceived(T frame, AbstractAioSession<T, R> session);


    /**
     * 事件
     * @param state
     * @param session
     * @param throwable
     */
    void event(State state, AbstractAioSession<T, R> session, Throwable... throwable);

    /**
     * start事件
     * @param lifeCycle
     */
    void started(LifeCycle lifeCycle);
}
