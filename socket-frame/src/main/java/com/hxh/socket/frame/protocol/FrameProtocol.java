package com.hxh.socket.frame.protocol;

import com.hxh.socket.core.FType;
import com.hxh.socket.frame.AbstractFrame;
import com.hxh.socket.frame.FrameFactory;
import com.hxh.socket.core.protocol.Protocol;
import com.hxh.socket.core.transport.AbstractAioSession;

import java.nio.ByteBuffer;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/25 9:16
 */
public class FrameProtocol implements Protocol<AbstractFrame, AbstractFrame> {

    @Override
    public AbstractFrame decode(ByteBuffer byteBuffer, AbstractAioSession<AbstractFrame, AbstractFrame> session) {
        AbstractFrame current = session.getCurrentRead();
        if (current == null) {
            current = FrameFactory.createFrame(byteBuffer.get());
            current.setFrom(session.getId());
            current.setType(FType.REQUEST);
            current.startWriteToFrame();
            session.setCurrentRead(current);
        }

        if (!byteBuffer.hasRemaining()) {
            return null;
        }

        int frameNeed = current.needWriting();

        int shouldRead = Math.min(frameNeed, byteBuffer.remaining());

        if (frameNeed > 0) {
            byte[] dst = new byte[shouldRead];
            byteBuffer.get(dst);
            current.writeToFrame(dst);
            if (byteBuffer.hasRemaining()) {
                return decode(byteBuffer, session);
            }
            if (current.needWriting() > 0) {
                return null;
            }
        }

        try {
            return current;
        } finally {
            session.setCurrentRead(null);
        }
    }

    @Override
    public byte[] encode(AbstractAioSession<AbstractFrame, AbstractFrame> session) {
        int length = AbstractFrame.MAX_READ_LENGTH;
        if (session.getCurrentWrite() != null) {
            if (session.getCurrentWrite().remainReading() > 0) {
                return session.getCurrentWrite().readFromFrameNew(length);
            } else {
                session.setCurrentWrite(null);
            }
        }

        AbstractFrame frame = session.getFrameSendQueue().poll();
        if (frame == null) {
            return null;
        } else {
            frame.startToRead();
            frame.setTo(session.getId());
            session.setCurrentWrite(frame);
            return session.getCurrentWrite().readFromFrameNew(length);
        }
    }
}