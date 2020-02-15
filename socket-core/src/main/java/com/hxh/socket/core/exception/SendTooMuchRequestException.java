package com.hxh.socket.core.exception;


/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/1/19 11:39
 */
public class SendTooMuchRequestException extends SendException {
    public SendTooMuchRequestException() {
    }

    public SendTooMuchRequestException(String message) {
        super(message);
    }

    public SendTooMuchRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendTooMuchRequestException(Throwable cause) {
        super(cause);
    }

    public SendTooMuchRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
