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
public class ReadCompletionHandler implements CompletionHandler<Integer, TcpAioSession> {

    private static final Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    @Override
    public void completed(Integer result, TcpAioSession session) {
        session.read(result);
    }

    @Override
    public void failed(Throwable exc, TcpAioSession session) {
        AioConfig config = session.getConfig();
        config.getProcessor().event(State.INPUT_EXCEPTION, session, exc);
        logger.info("call stopping begin,because read failed");
        session.stop(false);
    }
}
