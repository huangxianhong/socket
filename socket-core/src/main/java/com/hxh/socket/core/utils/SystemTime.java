package com.hxh.socket.core.utils;

import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 14:35
 */
public class SystemTime implements Time {
    @Override
    public long milliseconds() {
        return System.currentTimeMillis();
    }

    @Override
    public long hiResClockMs() {
        return TimeUnit.NANOSECONDS.toMillis(nanoseconds());
    }

    @Override
    public long nanoseconds() {
        return System.nanoTime();
    }

    @Override
    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // just wake up early
            Thread.currentThread().interrupt();
        }
    }
}
