package com.zhangxuan.net.socket.model;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.zhangxuan.net.socket.access.FrameFormatException;

public abstract class Frame {

	private String src = null;
	private String dst = null;
	private int id = 0;

	private int writeIndex = 0;
	private int writeTotal = 0;
	private byte[] writeCache = null;

	private int readIndex = 0;
	private int readTotal = 0;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void write(byte[] bs) throws IOException, FrameFormatException {
		if (bs == null || bs.length == 0)
			return;
		if (bs.length > getWriteRemain())
			throw new IllegalArgumentException("The writing bytes is longer than limit");
		int cachedLength = 0;
		if (writeCache != null && writeCache.length > 0) {
			cachedLength = writeCache.length;
			int newsize = cachedLength + bs.length;
			byte[] tmp = new byte[newsize];
			System.arraycopy(writeCache, 0, tmp, 0, writeCache.length);
			System.arraycopy(bs, 0, tmp, writeCache.length, bs.length);
			writeCache = tmp;
		} else {
			writeCache = bs;
		}

		if (writeIndex < getHeaderSize()) {
			if (writeCache.length < getHeaderSize()) {
				return;
			}
			byte[] headerBytes = new byte[getHeaderSize()];
			System.arraycopy(writeCache, 0, headerBytes, 0, getHeaderSize());
			writeHeader(headerBytes);
			writeIndex += getHeaderSize();
			if (writeCache.length < getHeaderSize())
				return;
			int remainLength = writeCache.length - getHeaderSize();
			byte[] remain = new byte[remainLength];
			System.arraycopy(writeCache, getHeaderSize(), remain, 0, remain.length);
			writeCache = remain;
			if (remainLength == 0)
				return;
		}
		int wi = writeBody(writeCache);
		writeIndex += wi;
		int remainLength = writeCache.length - wi;
		if (remainLength == 0) {
			writeCache = null;
			return;
		}
		if (remainLength > getWriteRemain())
			throw new FrameFormatException("write frame overflow");
		byte[] remain = new byte[remainLength];
		System.arraycopy(writeCache, wi, remain, 0, remainLength);
		writeCache = remain;
	}

	protected void writeHeader(byte[] headerBytes) throws FrameFormatException, IOException {
		ByteBuffer bf = ByteBuffer.wrap(headerBytes);
		int id = bf.getInt();
		setId(id);
		if (headerBytes.length == 4)
			return;
		byte[] remainHeaderByte = new byte[bf.remaining()];
		bf.get(remainHeaderByte);
		onWriteHeaderExt(remainHeaderByte);
	}

	protected int writeBody(byte[] bodyBytes) {
		int wi = onWriteBody(bodyBytes);
		return wi < 0 ? 0 : wi;
	}

	public abstract void onWriteHeaderExt(byte[] extHeaderBytes) throws FrameFormatException, IOException;

	public abstract int onWriteBody(byte[] extHeaderBytes);

	protected abstract int getHeaderSize();

	public void startToWrite() {
		setWriteTotal(getHeaderSize());
		setWriteIndex(0);
	}

	public void startToRead() {
		setReadTotal(getHeaderSize());
		setReadIndex(0);
	}

	public AckFrame replyFrame() {
		AckFrame ackFrame = new AckFrame();
		ackFrame.setDst(this.src);
		ackFrame.setDst(src);
		ackFrame.setId(this.id);
		return ackFrame;
	}

	public byte[] read(int limitSize) throws IOException {
		if (limitSize == 0)
			return null;
		if (limitSize > getReadRemain())
			limitSize = getReadRemain();
		if (limitSize == 0)
			return null;
		byte[] bs = null;
		if (readIndex < getHeaderSize()) {
			int headerlimit = getHeaderSize() - readIndex;
			if (headerlimit > limitSize)
				headerlimit = limitSize;
			byte[] headerBytes = readHeader(headerlimit);
			limitSize -= headerBytes.length;
			if (limitSize == 0 || readIndex < getHeaderSize())
				return headerBytes;
			bs = headerBytes;
		}
		byte[] bodyBytes = readBody(limitSize);
		if (bodyBytes == null)
			return null;
		limitSize -= bodyBytes.length;
		readIndex += bodyBytes.length;
		int headerLength = bs == null ? 0 : bs.length;
		byte[] rt = new byte[headerLength + bodyBytes.length];
		if (headerLength > 0)
			System.arraycopy(bs, 0, rt, 0, headerLength);
		System.arraycopy(bodyBytes, 0, rt, headerLength, bodyBytes.length);
		return rt;
	}

	protected byte[] readHeader(int limitSize) {
		if (limitSize == 0)
			return null;
		int headerRemain = getHeaderSize() - readIndex;
		if (headerRemain < limitSize)
			limitSize = headerRemain;
		byte[] baseHeaderBytes = null;
		if (readIndex < 4) {
			ByteBuffer bf = ByteBuffer.allocate(4);
			bf.putInt(getId());
			bf.flip();
			int l = 4 - readIndex;
			if (l > limitSize)
				l = limitSize;
			byte[] bs = new byte[l];
			bf.position(readIndex);
			bf.get(bs, 0, l);
			limitSize -= l;
			readIndex += l;
			if (limitSize == 0 || readIndex < 4)
				return bs;
			baseHeaderBytes = bs;
		}
		byte[] extHeaderBytes = readHeaderExt(limitSize);
		if (extHeaderBytes == null)
			return null;
		limitSize -= extHeaderBytes.length;
		readIndex += extHeaderBytes.length;
		int baseHeaderLength = baseHeaderBytes == null ? 0 : baseHeaderBytes.length;
		byte[] rt = new byte[baseHeaderLength + extHeaderBytes.length];
		if (baseHeaderLength > 0)
			System.arraycopy(baseHeaderBytes, 0, rt, 0, baseHeaderLength);
		System.arraycopy(extHeaderBytes, 0, rt, baseHeaderLength, extHeaderBytes.length);
		return rt;
	}

	protected byte[] readHeaderExt(int limitSize) {
		return onReadHeaderExt(limitSize);
	}

	protected abstract byte[] onReadHeaderExt(int limitSize);

	protected byte[] readBody(int limitSize) throws IOException {
		return onReadBody(limitSize);
	}

	protected abstract byte[] onReadBody(int limitSize) throws IOException;

	public int getWriteRemain() {
		return writeTotal - writeIndex - (writeCache == null ? 0 : writeCache.length);
	}

	public int getReadRemain() {
		return readTotal - readIndex;
	}

	protected void setWriteTotal(int writeTotal) {
		this.writeTotal = writeTotal;
	}

	protected void setReadTotal(int readTotal) {
		this.readTotal = readTotal;
	}

	protected int getWriteIndex() {
		return writeIndex;
	}

	protected int getReadIndex() {
		return readIndex;
	}

	protected void setWriteIndex(int writeIndex) {
		this.writeIndex = writeIndex;
	}

	protected void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}

	public static Frame createFrame(byte sign) {
		if ((sign & 0xf0) != 0xf0)
			return null;
		int ptc = sign & 0x0f;
		if (ptc == 0)
			return new AckFrame();
		else if (ptc == 1)
			return new PingFrame();
		else if (ptc == 2)
			return new BinaryFrame();
		else if (ptc == 3)
			return new TextFrame();
		else if (ptc == 15)
			return new CmdFrame();
		else
			return null;
	}

	public static byte getSign(Frame frame) {
		if (frame instanceof AckFrame)
			return (byte) 0xF0;
		else if (frame instanceof PingFrame)
			return (byte) 0xf1;
		else if (frame instanceof CmdFrame)
			return (byte) 0xff;
		else if (frame instanceof TextFrame)
			return (byte) 0xf3;
		else if (frame instanceof BinaryFrame)
			return (byte) 0xf2;
		else
			return 0x00;
	}
}
