package com.hxh.socket.core.exception;

import com.hxh.socket.core.SocketException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/3 12:40
 */
public class TimeoutException extends SocketException {
    public TimeoutException() {
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }

    public TimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
