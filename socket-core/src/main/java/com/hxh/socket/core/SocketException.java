package com.hxh.socket.core;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 10:25
 */
public class SocketException extends RuntimeException {
    public SocketException() {
    }

    public SocketException(String message) {
        super(message);
    }

    public SocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketException(Throwable cause) {
        super(cause);
    }

    public SocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
