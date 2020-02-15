package com.hxh.socket.frame;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 18:08
 */
public class BinaryFrame extends AbstractFrame {

    /**
     * header
     */
    private byte[] headerBytes;

    /**
     * body
     */
    private byte[] bodyBytes;

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
        this.headerBytes = bytes;
    }

    @Override
    public void writeBody(byte[] bytes) {
        this.bodyBytes = bytes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

    @Override
    public byte[] getHeaderBytes() {
        return headerBytes;
    }

    public void setHeaderBytes(byte[] headerBytes) {
        checkHeader();
        this.headerBytes = headerBytes;
    }

    @Override
    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        checkBody();
        this.bodyBytes = bodyBytes;
    }

    @Override
    public byte getSign() {
        return (byte) 0xF3;
    }
}
