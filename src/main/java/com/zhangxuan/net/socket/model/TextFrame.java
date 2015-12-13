package com.zhangxuan.net.socket.model;

import java.io.UnsupportedEncodingException;

public class TextFrame extends BinaryFrame {
	public String getText() throws UnsupportedEncodingException {
		byte[] data = getData();
		if (data == null)
			return "";
		return new String(data, "UTF-8");
	}

	public void setText(String content) {
		if (content == null)
			return;
		try {
			setData(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
