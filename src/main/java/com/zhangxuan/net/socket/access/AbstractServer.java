package com.zhangxuan.net.socket.access;

import java.util.Hashtable;

import com.zhangxuan.net.socket.model.Frame;

public abstract class AbstractServer implements Server {
	protected final String tag = this.getClass().getSimpleName();
	private ServerCallback serverCallback = null;
	private final Hashtable<String, Client> clients = new Hashtable<>();

	protected void register(Client client) {
		if (client == null)
			return;
		log("register", "ClientID : " + client.getId());
		synchronized (clients) {
			clients.put(client.getId(), client);
		}
	}

	protected void unregister(String clientId) {
		if (clientId == null)
			return;
		log("unregister", "ClientID : " + clientId);
		synchronized (clients) {
			clients.remove(clientId);
		}
	}

	protected Client findClient(String clientId) {
		if (clientId == null)
			return null;
		Client client = null;
		synchronized (clients) {
			client = clients.get(clientId);
		}
		return client;
	}

	protected Hashtable<String, Client> getClients() {
		Hashtable<String, Client> tmp = new Hashtable<String, Client>();
		synchronized (clients) {
			tmp.putAll(clients);
		}
		return tmp;
	}

	@Override
	public void onConnected(Client client) {
		log("onConnected", "Client connected " + client.getId());
		if (client instanceof AsyncTcpClient)
			register(client);
		if (serverCallback != null)
			serverCallback.onConnected(client);
	}

	@Override
	public void onDisconnected(Client client) {
		if (client == null)
			return;
		log("onDisconnected", "Client disconnected " + client.getId());
		if (serverCallback != null)
			serverCallback.onDisconnected(client);
		unregister(client.getId());
	}

	@Override
	public void onFrameReceived(Frame frame) {
		log("onFrameRead", "Received " + frame.getClass().getSimpleName() + " frame.\tFrameID=" + frame.getId());
		if (serverCallback != null)
			try {
				serverCallback.onFrameReceived(frame);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void onFrameSent(String frameId) {
		log("onFrameSent", "Frame Sent " + frameId);
		if (serverCallback != null)
			serverCallback.onFrameSent(frameId);
	}

	protected void log(String method, String msg) {
		System.out.println(tag + " :: " + method + " : " + msg);
	}

	protected void setCallback(ServerCallback callback) {
		this.serverCallback = callback;
	}

	protected synchronized ServerCallback getCallback() {
		return serverCallback;
	}
}
