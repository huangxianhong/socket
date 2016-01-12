package com.zhangxuan.net.socket.model;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.zhangxuan.net.socket.access.FrameFormatException;

public class BinaryFrame extends Frame {

	public static long maxFrameLength = 1024 * 1024;
	private int dataLength = 0;
	private byte[] data = null;

	@Override
	public void startToRead() {
		super.startToRead();
		if (data != null) {
			setReadTotal(data.length + getHeaderSize());
		}
	}

	public int getDataLength() {
		return dataLength;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		dataLength = data == null ? 0 : data.length;
	}

	@Override
	protected int getHeaderSize() {
		return 8;
	}

	@Override
	protected byte[] onReadHeaderExt(int limitSize) {
		ByteBuffer bf = ByteBuffer.allocate(4);
		bf.putInt(getDataLength());
		bf.flip();
		int ri = getReadIndex();
		int l = 8 - ri;
		if (l > limitSize)
			l = limitSize;
		byte[] bs = new byte[l];
		bf.position(getReadIndex() - 4);
		bf.get(bs, 0, l);
		return bs;
	}

	@Override
	protected byte[] onReadBody(int limitSize) throws IOException {
		if (limitSize == 0)
			return null;
		int rr = getReadRemain();
		if (limitSize > rr)
			limitSize = rr;
		byte[] bs = new byte[limitSize];
		System.arraycopy(data, getReadIndex() - getHeaderSize(), bs, 0, limitSize);
		return bs;
	}

	@Override
	public void onWriteHeaderExt(byte[] extHeaderBytes) throws FrameFormatException {
		ByteBuffer bf = ByteBuffer.wrap(extHeaderBytes);
		int i = bf.getInt();
		if (i > maxFrameLength)
			throw new FrameFormatException("Frame size:" + i + " is langer than maxLength:" + maxFrameLength);
		if (i < 0)
			throw new FrameFormatException("Frame dataLength<0");
		dataLength = i;
		setWriteTotal(getHeaderSize() + dataLength);
	}

	@Override
	public int onWriteBody(byte[] body) {
		if (body == null)
			return 0;
		if (data == null) {
			data = body;
			return body.length;
		}
		int newsize = data.length + body.length;
		byte[] newdata = new byte[newsize];
		System.arraycopy(data, 0, newdata, 0, data.length);
		System.arraycopy(body, 0, newdata, data.length, body.length);
		data = newdata;
		return body.length;
	}

}
