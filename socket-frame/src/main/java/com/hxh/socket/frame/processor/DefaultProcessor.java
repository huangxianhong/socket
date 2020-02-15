package com.hxh.socket.frame.processor;

import com.hxh.socket.core.*;
import com.hxh.socket.core.processor.RequestProcessor;
import com.hxh.socket.frame.AbstractFrame;
import com.hxh.socket.core.processor.Processor;
import com.hxh.socket.core.transport.AbstractAioSession;
import com.hxh.socket.core.transport.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 16:12
 */
public class DefaultProcessor implements Processor<AbstractFrame, AbstractFrame> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

    private final HashMap<Byte, Pair<RequestProcessor, ExecutorService>> processorTable = new HashMap<>(64);

    private ExecutorService publicExecutor = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("AIO-PUBLIC-POOL"));

    private Pair<RequestProcessor, ExecutorService> defaultRequestProcessor;

    public void registerRequestProcessor(byte sign, RequestProcessor requestProcessor, ExecutorService executor) {
        ExecutorService executorThis = executor == null ? publicExecutor : executor;
        this.processorTable.put(sign, new Pair<>(requestProcessor, executorThis));
    }

    public void registerDefaultRequestProcessor(RequestProcessor requestProcessor, ExecutorService executor) {
        this.defaultRequestProcessor = new Pair<>(requestProcessor, executor);
    }

    @Override
    public void frameSent(AbstractFrame frame, AbstractAioSession<AbstractFrame, AbstractFrame> session) {
        // logger.info("frameSent={},src={}", frame, session.getId());
    }

    @Override
    public void frameReceived(AbstractFrame frame, AbstractAioSession<AbstractFrame, AbstractFrame> session) {
        logger.debug("frameReceived={},src={}", frame, session.getId());
        if (Mode.SERVER.equals(session.getMode())) {
            if (FType.REQUEST.equals(frame.getType()) && OneWayType.NO.equals(frame.getOneWayType())) {
                frame.setType(FType.RESPONSE);
                frame.setOneWayType(OneWayType.YES);
                session.sendOneWay(frame);
            }
        }
    }

    @Override
    public void event(State state, AbstractAioSession<AbstractFrame, AbstractFrame> session, Throwable... throwable) {
        logger.debug("event state={},session={},throwable={}", state, session, throwable);
    }

    @Override
    public void started(LifeCycle lifeCycle) {

    }
}
