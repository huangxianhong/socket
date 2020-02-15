package com.hxh.socket.frame;

import com.hxh.socket.frame.FrameFactory;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 17:51
 */
public class FrameFactoryTest {
    public static void main(String[] args) {
        FrameFactory.createFrame((byte) 0xf3);
    }
}
