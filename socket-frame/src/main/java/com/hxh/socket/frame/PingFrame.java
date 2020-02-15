package com.hxh.socket.frame;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 18:08
 */
public class PingFrame extends AbstractFrame {
    @Override
    public int getHeaderLengthSize() {
        return 4;
    }

    @Override
    public int getTotalLengthSize() {
        return 4;
    }

    @Override
    public void writeHeader(byte[] bytes) {

    }

    @Override
    public void writeBody(byte[] bytes) {

    }

    @Override
    public byte[] getHeaderBytes() {
        return new byte[0];
    }

    @Override
    public byte[] getBodyBytes() {
        return new byte[0];
    }

    @Override
    public byte getSign() {
        return (byte) 0xF1;
    }
}
