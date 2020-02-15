package com.hxh.socket.core.transport;

import com.hxh.socket.core.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.CompletionHandler;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 11:15
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, TcpAioSession> {

    private static final Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    @Override
    public void completed(Integer result, TcpAioSession session) {
        session.written(result);
    }

    @Override
    public void failed(Throwable exc, TcpAioSession session) {
        AioConfig config = session.getConfig();
        config.getProcessor().event(State.OUTPUT_EXCEPTION, session, exc);
        logger.info("call stopping begin,because write failed");
        session.stop(false);
    }
}
