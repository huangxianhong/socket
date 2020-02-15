package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:29
 */
public interface SendAble<T extends IFrame, R extends IFrame> {

    /**
     * 异步发送
     * @param frame
     * @param sendCallback
     */
    void sendAsync(T frame, SendCallback sendCallback);


    /**
     * 同步返送
     * @param frame
     * @return
     */
    R sendSync(T frame);


    /**
     * 单向发送
     * @param frame
     */
    void sendOneWay(T frame);
}
