package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:33
 */
public interface SendCallback {

    /**
     * 发送回调
     * @param responseFuture
     */
    void invoke(final ResponseFuture responseFuture);
}
