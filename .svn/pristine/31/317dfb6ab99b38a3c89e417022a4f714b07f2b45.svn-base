package com.zhangxuan.net.socket.model;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.zhangxuan.net.socket.access.FrameFormatException;

public class BinaryFrame extends Frame {

	public static long maxFrameLength = 1024 * 1024;
	private int dataLength = 0;
	private byte[] data = null;

	// @Override
	// protected int onWrite(byte[] bs) throws IOException {
	// int wi = 0;
	// int rr = getWriteIndex();
	// if (rr < 8) {
	// if (bs.length < getWriteRemain())
	// return 0;
	// ByteBuffer bf = ByteBuffer.wrap(bs);
	// int id = bf.getInt();
	// int len = bf.getInt();
	// wi += 8;
	// setId(id);
	// if (len > MAX_FRAME_SIZE)
	// throw new IOException("Frame size too big");
	// setDataLength(len);
	// len += 8;
	// setWriteTotal(len);
	// return wi;
	// }
	// if (data == null) {
	// data = bs;
	// return bs.length;
	// }
	// int newSize = data.length + bs.length;
	// byte[] tmp = new byte[newSize];
	// System.arraycopy(data, 0, tmp, 0, data.length);
	// System.arraycopy(bs, 0, tmp, data.length, bs.length);
	// data = tmp;
	// return bs.length;
	// }

	@Override
	public void startToRead() {
		super.startToRead();
		if (data != null) {
			setReadTotal(data.length + getHeaderSize());
		}
	}

	// @Override
	// protected byte[] onRead(int limit) throws IOException {
	// int ri = getReadIndex();
	// int rr = getReadRemain();
	// if (rr < limit)
	// limit = rr;
	// byte[] tmp = null;
	// if (ri <= 8) {
	// ByteBuffer bf = ByteBuffer.allocate(8);
	// bf.putInt(getId());
	// if (data != null)
	// bf.putInt(data.length);
	// else
	// bf.putInt(0);
	// bf.flip();
	// int t = 8 - ri;
	// if (t >= 0) {
	// tmp = new byte[t];
	// bf.get(tmp, ri, t);
	// limit -= t;
	// }
	// }
	// if (limit <= 0)
	// return tmp;
	// byte[] tmpdata = new byte[limit];
	// if (tmp == null) {
	// System.arraycopy(data, ri - 8, tmpdata, 0, limit);
	// return tmpdata;
	// }
	// ByteBuffer bf = ByteBuffer.allocate(tmp.length + tmpdata.length);
	// bf.put(tmp);
	// System.arraycopy(data, 0, tmpdata, 0, limit);
	// bf.put(tmpdata);
	// return bf.array();
	// }

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
