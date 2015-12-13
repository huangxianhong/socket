package com.zhangxuan.net.socket.convergence;

import com.zhangxuan.net.socket.model.Frame;

public interface Communication {
	void start();

	void stop();

	void connect(String hostId) throws Exception;

	void disconnect(String hostId);

	void send(String dstHostId, Frame frame) throws Exception;

	boolean isHostOnline(String hostId);

}
