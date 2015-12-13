package com.zhangxuan.net.socket.access;

import com.zhangxuan.net.socket.model.Frame;

public interface Client {
	String getId();

	void send(Frame frame);

	void ping();

	void start();

	void stop();

	void setParameter(String key, String value);

	String getParameter(String key);
}
