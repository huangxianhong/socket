package com.hxh.socket.core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 11:15
 */
public class AcceptCompletionHandler<T> implements CompletionHandler<AsynchronousSocketChannel, AbstractRemote> {

    private static final Logger logger = LoggerFactory.getLogger(AcceptCompletionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel result, AbstractRemote remote) {
        remote.accepted(result);
    }

    @Override
    public void failed(Throwable exc, AbstractRemote remote) {
        logger.error("accept error:", exc);
    }
}
