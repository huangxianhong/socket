package com.hxh.socket.core.transport;

import com.hxh.socket.core.IFrame;
import com.hxh.socket.core.NamedThreadFactory;
import com.hxh.socket.core.ResponseFuture;
import com.hxh.socket.core.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/26 14:20
 */
public abstract class AbstractRemote<T extends IFrame, R extends IFrame> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRemote.class);

    /**
     * 扫描响应等待列表回调执行线程池
     */
    private ThreadPoolExecutor scanExecutor = new ThreadPoolExecutor(32, 32, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100), new NamedThreadFactory("AIO-SCAN-RESPONSE-CALLBACK-POOL"));


    /**
     * 扫描响应等待列表定时器
     */
    private ScheduledExecutorService timer = Executors.newScheduledThreadPool(1, new NamedThreadFactory("AIO-SCAN-RESPONSE-POOL"));

    /**
     * 需要等待发送反馈的等待列表
     */
    protected ConcurrentHashMap<String, ResponseFuture<T, R>> responseTable = new ConcurrentHashMap<>();

    /**
     * 接收连接
     *
     * @param channel 通道
     */
    abstract void accepted(AsynchronousSocketChannel channel);


    /**
     * 扫描返回结果等待对象，处理超时的情况
     */
    void scanResponseTable() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scan();
            }
        }, 1000 * 3, 1000, TimeUnit.MILLISECONDS);
    }


    /**
     * 扫描
     */
    private void scan() {
        long delay = 2000;
        List<ResponseFuture<T, R>> rfList = new LinkedList();
        Predicate<Map.Entry<String, ResponseFuture<T, R>>> predicate = (e) -> {
            ResponseFuture<T, R> rep = e.getValue();
            if ((rep.getBegin() + rep.getTimeout() + delay) <= Time.SYSTEM.milliseconds()) {
                rfList.add(rep);
                logger.warn("remove timeout request, " + rep);
                return true;
            }
            return false;
        };

        responseTable.entrySet().removeIf(predicate);

        for (ResponseFuture<T, R> rf : rfList) {
            scanExecutor.execute(() -> {
                try {
                    rf.execute();
                } catch (Throwable e) {
                    logger.warn("scanResponseTable, operationComplete Exception", e);
                }
            });
        }
    }
}
