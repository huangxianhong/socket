package com.hxh.socket.frame;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 18:09
 */
public class PongFrame extends PingFrame {
    @Override
    public byte getSign() {
        return (byte) 0xF2;
    }
}
