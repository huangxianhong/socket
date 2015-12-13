package com.zhangxuan.net.socket.access;



public interface ServerCallback extends ClientCallback {
	void onStarted(Server server);
	void onStoped(Server server);
	void onAccepted(Client client);
}
