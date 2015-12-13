package com.zhangxuan.net.socket.convergence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.zhangxuan.net.socket.access.AsyncTcpServer;
import com.zhangxuan.net.socket.access.Client;
import com.zhangxuan.net.socket.access.FrameTargetException;
import com.zhangxuan.net.socket.access.Server;
import com.zhangxuan.net.socket.access.ServerCallback;
import com.zhangxuan.net.socket.model.AckFrame;
import com.zhangxuan.net.socket.model.BinaryFrame;
import com.zhangxuan.net.socket.model.Cmd;
import com.zhangxuan.net.socket.model.CmdFrame;
import com.zhangxuan.net.socket.model.CmdUtil;
import com.zhangxuan.net.socket.model.Frame;
import com.zhangxuan.net.socket.model.TextFrame;

public class StandardCommunication implements Communication {

	private Config config = null;
	private CommunicationCallback callback = null;
	private Server server = null;
	private final CacheData data = new CacheData();
	private ServerCallback serverCallback = new ServerCallback() {

		public void onFrameSent(String frameId) {
			StandardCommunication.this.onFrameSent(frameId);
		}

		public void onFrameReceived(Frame frame) throws Exception {
			StandardCommunication.this.onFrameReceived(frame);
		}

		@Override
		public void onDisconnected(Client client) {
			StandardCommunication.this.onDisconnected(client);
		}

		@Override
		public void onConnected(Client client) {
			StandardCommunication.this.onConnected(client);
		}

		@Override
		public void onAccepted(Client client) {
			StandardCommunication.this.onAccepted(client);
		}

		@Override
		public void onStarted(Server server) {
			StandardCommunication.this.onStarted(server);
		}

		@Override
		public void onStoped(Server server) {
			StandardCommunication.this.onStoped(server);
		}
	};

	public StandardCommunication(Config config, CommunicationCallback callback) throws IOException, Exception {
		if (config == null || callback == null)
			throw new IllegalArgumentException("config or callback is null");
		this.config = config;
		this.callback = callback;
		HostInfo info = config.getInfo();
		if (info == null)
			throw new IllegalArgumentException("config.info is null");
		server = new AsyncTcpServer(null, info.getPort(), serverCallback);
	}

	public void start() {
		synchronized (this) {
			if (server != null)
				server.start();
		}
	}

	public void stop() {
		synchronized (this) {
			if (server != null)
				server.stop();
		}
	}

	public void connect(String hostId) throws IOException {
		if (hostId == null || hostId.trim().length() == 0)
			throw new IllegalArgumentException("hostId is null");
		log("connect", "HostId=" + hostId);
		Hashtable<String, HostInfo> hosts = config.getHosts();
		HostInfo h = null;
		h = hosts.get(hostId);
		if (h == null)
			throw new IllegalArgumentException("hostId is unknown : " + hostId);
		String host = h.getHost();
		int port = h.getPort();
		Client client = server.connect(host, port);
		if (client != null)
			data.putClientInfo(client.getId(), "tmp-hostid", hostId);
	}

	public void disconnect(String hostId) {
		if (hostId == null)
			return;
		log("disconnect", "HostId=" + hostId);
		ArrayList<String> clients = data.getHostClient(hostId);
		if (clients == null)
			return;
		synchronized (clients) {
			for (String clientId : clients) {
				server.close(clientId);
			}
		}
	}

	public void send(String dstHostId, Frame frame) throws FrameTargetException {
		if (frame == null)
			throw new IllegalArgumentException("Frame is null");
		log("send", frame.toString());
		frame.setSrc(config.getInfo().getId());
		ArrayList<String> clients = this.data.getHostClient(dstHostId);
		if (clients == null || clients.size() == 0)
			throw new IllegalStateException("Dst host is not online");
		String clientid = clients.get(0);
		frame.setDst(clientid);
		server.send(frame);
	}

	public void sendBinary(String dstHostId, byte[] data) throws FrameTargetException {
		if (data == null)
			return;
		BinaryFrame bf = new BinaryFrame();
		bf.setData(data);
		send(dstHostId, bf);
	}

	public void sendText(String dstHostId, String text) throws FrameTargetException {
		if (text == null)
			return;
		TextFrame tf = new TextFrame();
		tf.setText(text);
		send(dstHostId, tf);
	}

	protected void onFrameSent(String frameId) {

	}

	protected void onFrameReceived(Frame frame) throws Exception {
		if (frame == null)
			return;
		String srcclient = frame.getSrc();
		String srchost = data.getClientInfo(srcclient, "hostid");
		if (frame instanceof CmdFrame) {
			CmdFrame cf = (CmdFrame) frame;
			onCmdFrameReceived(cf);
		} else if (frame instanceof TextFrame) {
			TextFrame sf = (TextFrame) frame;
			sf.setSrc(srchost);
			callback.onFrameReceived(sf);
		} else if (frame instanceof BinaryFrame) {
			BinaryFrame bf = (BinaryFrame) frame;
			bf.setSrc(srchost);
			callback.onFrameReceived(bf);
		} else if (frame instanceof AckFrame) {

		} else {
			frame.setSrc(srchost);
			if (callback != null)
				callback.onFrameReceived(frame);
			return;
		}
	}

	protected void onCmdFrameReceived(CmdFrame frame) {
		Cmd reqCmd = frame.getCmd();
		if (reqCmd == null)
			return;
		String clientId = frame.getSrc();
		if ("Hello".equals(reqCmd.getName())) {
			if (!checkAuth(reqCmd))
				server.close(frame.getSrc());
			data.registerHost(reqCmd.getParameter("srcHost"), clientId, callback);
			Cmd rplCmd = CmdUtil.createHi();
			rplCmd.setParameter("srcHost", config.getInfo().getId());
			CmdFrame cmdFrame = new CmdFrame();
			cmdFrame.setDst(frame.getSrc());
			cmdFrame.setCmd(rplCmd);
			try {
				server.send(cmdFrame);
			} catch (FrameTargetException e) {
			}
		} else if ("Hi".equals(reqCmd.getName())) {
			data.registerHost(reqCmd.getParameter("srcHost"), clientId, callback);
		} else {
			if (callback != null)
				callback.onCmdReceived(reqCmd);
		}
	}

	protected void onDisconnected(Client client) {
		if (client == null)
			return;
		String clientId = client.getId();
		log("onDisconnected", clientId);
		data.unregisterHostClient(clientId, callback);
		data.delClientInfo(clientId);
	}

	protected void onConnected(Client client) {
		log("onConnected", client.getId());
		String clientId = client.getId();
		String clientHostId = data.getClientInfo(clientId, "tmp-hostid");
		if (clientHostId == null) {
			log("onConnected", "***Unknown hostid at client " + client.getId() + "");
			return;
		}
		HostInfo h = config.getHosts().get(clientHostId);
		if (h == null) {
			log("onConnected", "***HostId " + clientHostId + " is not exist for  client " + client.getId() + "");
			return;
		}
		sendHelloCmd(client, h);
	}

	protected void onAccepted(Client client) {

	}

	protected void onStarted(Server server) {
		callback.onStarted(getId());
	}

	protected void onStoped(Server server) {
		callback.onStoped(getId());
	}

	private void sendHelloCmd(Client client, HostInfo dstHostInfo) {
		HostInfo myInfo = config.getInfo();
		Cmd cmd = CmdUtil.createHello(myInfo.getId(), myInfo.getSecurityCode(), dstHostInfo.getId(), dstHostInfo.getSecurityCode());
		CmdFrame cf = new CmdFrame();
		cf.setCmd(cmd);
		client.send(cf);
	}

	private boolean checkAuth(Cmd cmd) {
		String srcHost = cmd.getParameter("srcHost");
		String srcCode = cmd.getParameter("srcCode");
		String dstHost = cmd.getParameter("dstHost");
		String dstCode = cmd.getParameter("dstCode");
		HostInfo sh = config.getHosts().get(srcHost);
		HostInfo dh = config.getInfo();
		if (sh == null)
			return false;
		if (!dh.getId().equals(dstHost))
			return false;
		if (!dh.getSecurityCode().equals(dstCode))
			return false;
		if (!sh.getSecurityCode().equals(srcCode))
			return false;
		return true;
	}

	protected void log(String method, String msg) {
		System.out.println(this.toString() + " :: " + method + " : " + msg);
	}

	@Override
	public boolean isHostOnline(String hostId) {
		ArrayList<String> hostClients = data.getHostClient(hostId);
		if (hostClients != null && hostClients.size() > 0) {
			return true;
		}
		return false;
	}

	public String getId() {
		return config.getInfo().getId();
	}

}