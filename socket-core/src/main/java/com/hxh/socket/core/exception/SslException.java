package com.hxh.socket.core.exception;

import com.hxh.socket.core.SocketException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 17:03
 */
public class SslException extends SocketException {
    public SslException() {
    }

    public SslException(String message) {
        super(message);
    }

    public SslException(String message, Throwable cause) {
        super(message, cause);
    }

    public SslException(Throwable cause) {
        super(cause);
    }

    public SslException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
