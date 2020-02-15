package com.hxh.socket.core.exception;

import com.hxh.socket.core.SocketException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/31 17:41
 */
public class SendException extends SocketException {
    public SendException() {
    }

    public SendException(String message) {
        super(message);
    }

    public SendException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendException(Throwable cause) {
        super(cause);
    }

    public SendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
