package com.zhangxuan.net.socket.convergence;

import com.zhangxuan.net.socket.access.Client;
import com.zhangxuan.net.socket.model.Cmd;
import com.zhangxuan.net.socket.model.Frame;

public interface CommunicationCallback {
//	void onDataReceived(Frame frame);
//
//	void onDataSent(String id);

	void onCmdReceived(Cmd cmd);

	void onCmdSent(String id);

	void onHostOnline(String hostId);

	void onHostOffline(String hostId);

	void onConnectionAccepted(Client client);

	void onFrameReceived(Frame frame);

	void onFrameSent(Frame frame);

	void onConnectionDisconnected(Client client);

	void onConnectionConnected(Client client);

	void onStarted(String id);

	void onStoped(String id);
}