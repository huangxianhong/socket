package com.hxh.socket.core;

import com.hxh.socket.core.utils.Time;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/31 15:50
 */
public class ResponseFuture<T extends IFrame, R extends IFrame> {
    private String frameId;
    private T request;
    private R response;

    private long begin;

    private long timeout;

    private SendCallback callback;

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ResponseFuture(String frameId, T request, SendCallback callback, long timeout) {
        this.frameId = frameId;
        this.request = request;
        this.callback = callback;
        this.timeout = timeout;
        this.begin = Time.SYSTEM.milliseconds();
    }

    public ResponseFuture(String frameId, T request, long timeout) {
        this.frameId = frameId;
        this.request = request;
        this.timeout = timeout;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public R getResponse() {
        return response;
    }

    public void setResponse(R response) {
        this.response = response;
        countDownLatch.countDown();
    }

    public boolean waitResponse() throws InterruptedException {
        return countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
    }

    public SendCallback getCallback() {
        return callback;
    }

    public void setCallback(SendCallback callback) {
        this.callback = callback;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void execute() {
        if (callback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                callback.invoke(this);
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("frameId", frameId)
                .append("begin", begin)
                .append("timeout", timeout)
                .toString();
    }
}
