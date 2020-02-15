package com.hxh.socket.core.transport;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.LifeCycle;
import com.hxh.socket.core.SendAble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:54
 */
public abstract class AbstractAioSession<T extends IFrame, R extends IFrame> implements LifeCycle, SendAble<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAioSession.class);

    private Mode mode;

    /**
     * sessionId
     * @return sessionId
     */
    public abstract String getId();


    /**
     * 停止
     * @param immediate 是否立即停止
     */
    public abstract void stop(boolean immediate);

    /**
     * Frame的发送队列
     * @return frame队列
     */
    public abstract ConcurrentLinkedQueue<T> getFrameSendQueue();


    /**
     * 当前收到的Frame
     * @return frame
     */
    public abstract T getCurrentRead();

    /**
     * 设置当前读
     * @param currentRead frame
     */
    public abstract void setCurrentRead(T currentRead);

    /**
     * 当前正在发送的Frame
     * @return frame
     */
    public abstract T getCurrentWrite();

    /**
     * 设置当前写
     * @param currentWrite frame
     */
    public abstract void setCurrentWrite(T currentWrite);

    /**
     * 当前session是客户端还是服务端
     * @return mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * 设置session模式
     * @param mode mode
     */
    void setMode(Mode mode) {
        this.mode = mode;
    }


    /**
     * 理解停止当前这个session
     */
    @Override
    public void stop() {
        logger.info("call stopping begin");
        stop(true);
    }
}
