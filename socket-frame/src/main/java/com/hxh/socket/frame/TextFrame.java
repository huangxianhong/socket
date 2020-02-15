package com.hxh.socket.frame;

import com.hxh.socket.core.FType;
import com.hxh.socket.core.utils.BufferUtil;
import com.hxh.socket.core.utils.MapUtils;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2019/12/24 18:09
 */
public class TextFrame extends BinaryFrame {

    private Map<String, String> header;

    private String data;

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
        super.setHeaderBytes(MapUtils.serialize(header));
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        super.setBodyBytes(data.getBytes());
    }

    @Override
    public void writeBody(byte[] bytes) {
        super.writeBody(bytes);
        data = new String(bytes);
    }

    @Override
    public void writeHeader(byte[] bytes) {
        ByteBuffer byteBuffer = BufferUtil.wrap(bytes);
        byte type = byteBuffer.get();
        int opaque = byteBuffer.getInt();
        int len = byteBuffer.get();
        byte[] b = new byte[len];
        byteBuffer.get(b);
        String frameId = new String(b);
        long createTime = byteBuffer.getLong();

        b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);

        setType(FType.valueOf(type));
        setOpaque(opaque);
        setFrameId(frameId);
        setCreateTime(createTime);
        super.writeHeader(b);
        header = MapUtils.deserialize(b);
    }

    @Override
    public byte getSign() {
        return (byte) 0xF4;
    }
}
