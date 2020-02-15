package com.hxh.socket.core.transport;


/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 13:41
 */
public class AioServerConfig extends AioConfig {

    private int backlog = 1000;
    private int corePoolSize = 30;
    private int maximumPoolSize = 30;
    private int queueSize = 1000;

    /**
     * 线程池核心数
     */
    private int acceptCorePoolSize = 20;

    /**
     * 线程池最大数
     */
    private int acceptMaximumPoolSize = 20;

    /**
     * 接收连接线程池队列大小
     */
    private int acceptQueueSize = 1000;

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getAcceptCorePoolSize() {
        return acceptCorePoolSize;
    }

    public void setAcceptCorePoolSize(int acceptCorePoolSize) {
        this.acceptCorePoolSize = acceptCorePoolSize;
    }

    public int getAcceptMaximumPoolSize() {
        return acceptMaximumPoolSize;
    }

    public void setAcceptMaximumPoolSize(int acceptMaximumPoolSize) {
        this.acceptMaximumPoolSize = acceptMaximumPoolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getAcceptQueueSize() {
        return acceptQueueSize;
    }

    public void setAcceptQueueSize(int acceptQueueSize) {
        this.acceptQueueSize = acceptQueueSize;
    }
}
