package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 15:37
 */
public enum State {
    /**
     * 新建session
     */
    NEW_SESSION,

    /**
     * 读异常
     */
    INPUT_EXCEPTION,
    /**
     * 写异常
     */
    OUTPUT_EXCEPTION,
    /**
     * 会话关闭
     */
    SESSION_STOPPED,
    /**
     * 会话关闭中
     */
    SESSION_STOPPING
}
