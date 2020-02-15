package com.hxh.socket.core;

import com.hxh.socket.core.transport.AbstractAioSession;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:26
 */
public interface Client extends LifeCycle {
    /**
     * 启动
     * @return session
     */
    AbstractAioSession start0();
}
