package com.hxh.socket.core.exception;

import com.hxh.socket.core.SocketException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/11 20:16
 */
public class SessionException extends SocketException {
    public SessionException() {
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

    public SessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
