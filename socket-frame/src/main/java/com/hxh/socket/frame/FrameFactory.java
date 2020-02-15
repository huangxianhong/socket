package com.hxh.socket.frame;

import com.hxh.socket.core.exception.FrameException;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 17:48
 */
public class FrameFactory {
    public static AbstractFrame createFrame(byte sign) {
        int b = 0xf0;
        if ((sign & b) != b) {
            throw new FrameException("illegal frame");
        }

        int type = sign & 0x0f;
        switch (type) {
            case 1:
                return new PingFrame();
            case 2:
                return new PongFrame();
            case 3:
                return new BinaryFrame();
            case 4:
                return new TextFrame();
            default:
                return null;

        }
    }
}
