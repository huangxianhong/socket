package com.zhangxuan.net.socket.model;

import java.io.IOException;

public class PingFrame extends Frame {

	@Override
	protected int getHeaderSize() {
		return 4;
	}

	@Override
	public void onWriteHeaderExt(byte[] extHeaderBytes) {
	}

	@Override
	public int onWriteBody(byte[] extHeaderBytes) {
		return 0;
	}

	@Override
	protected byte[] onReadHeaderExt(int limitSize) {
		return null;
	}

	@Override
	protected byte[] onReadBody(int limitSize) throws IOException {
		return null;
	}

}
