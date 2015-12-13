package com.zhangxuan.net.socket.access;

import com.zhangxuan.net.socket.model.Frame;

public interface ClientCallback {
	void onConnected(Client client);

	void onDisconnected(Client client);

	void onFrameReceived(Frame frame) throws Exception;

	void onFrameSent(String frameId);
}
