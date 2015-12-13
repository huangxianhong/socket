package com.zhangxuan.net.socket.access;

import java.io.IOException;

import com.zhangxuan.net.socket.model.Frame;

public interface Server extends Runnable, ClientCallback {

	void send(Frame frame) throws FrameTargetException;

	Client connect(String host, int port) throws IOException;

	void close(String ClientId);

	void start();

	void stop();

}
