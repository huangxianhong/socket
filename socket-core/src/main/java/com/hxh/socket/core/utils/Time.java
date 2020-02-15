package com.hxh.socket.core.utils;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 14:35
 */
public interface Time {
    Time SYSTEM = new SystemTime();

    /**
     * Returns the current time in milliseconds.
     * @return
     */
    long milliseconds();

    /**
     * Returns the value returned by `nanoseconds` converted into milliseconds.
     * @return
     */
    long hiResClockMs();

    /**
     * Returns the current value of the running JVM's high-resolution time source, in nanoseconds.
     *
     * <p>This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     * The value returned represents nanoseconds since some fixed but
     * arbitrary <i>origin</i> time (perhaps in the future, so values
     * may be negative).  The same origin is used by all invocations of
     * this method in an instance of a Java virtual machine; other
     * virtual machine instances are likely to use a different origin.
     * @return
     */
    long nanoseconds();

    /***
     * Sleep for the given number of milliseconds
     * @param ms
     */
    void sleep(long ms);
}
