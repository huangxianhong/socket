package com.hxh.socket.core.exception;

import com.hxh.socket.core.SocketException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/11 11:52
 */
public class HandshakeException extends SocketException {
    public HandshakeException() {
    }

    public HandshakeException(String message) {
        super(message);
    }

    public HandshakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandshakeException(Throwable cause) {
        super(cause);
    }

    public HandshakeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
