package com.zhangxuan.net.socket.convergence;

import java.util.ArrayList;
import java.util.Hashtable;

public class CacheData {
	private final Hashtable<String, ArrayList<String>> hostClients = new Hashtable<>();
	private final Hashtable<String, Hashtable<String, String>> clientInfo = new Hashtable<>();

	// HostCients
	protected void registerHost(String hostid, String clientId, CommunicationCallback callback) {
		if (hostid == null || clientId == null)
			return;
		log("registerHost", "hostid=" + hostid + "\tclientid=" + clientId);
		ArrayList<String> list = null;
		synchronized (hostClients) {
			list = hostClients.get(hostid);
			if (list == null) {
				list = new ArrayList<>();
				hostClients.put(hostid, list);
			}
		}
		synchronized (list) {
			list.add(clientId);
		}
		putClientInfo(clientId, "hostid", hostid);
		log("registerHost", "Host " + hostid + " -> " + clientId + " is online.");
		if (callback != null)
			try {
				callback.onHostOnline(hostid);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	protected void unregisterHostClients(String hostId, CommunicationCallback callback) {
		if (hostId == null)
			return;
		log("unregisterHost", "hostid=" + hostId);
		ArrayList<String> clients = null;
		synchronized (hostClients) {
			clients = hostClients.get(hostId);
			hostClients.remove(hostId);
		}
		if (clients == null)
			return;
		synchronized (clientInfo) {
			for (String clientid : clients) {
				clientInfo.remove(clientid);
			}
		}
		log("unregisterHost", "Host " + hostId + " is offline.");
		if (callback != null)
			try {
				callback.onHostOffline(hostId);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	protected void unregisterHostClient(String clientid, CommunicationCallback callback) {
		if (clientid == null)
			return;
		String hid = getClientInfo(clientid, "hostid");
		if (hid == null)
			return;
		ArrayList<String> clients = null;
		synchronized (hostClients) {
			clients = hostClients.get(hid);
		}
		if (clients == null)
			return;
		synchronized (clients) {
			clients.remove(clientid);
		}
		log("unregisterHostClient", "Host " + hid + " ->" + clientid + " is offline.");
		synchronized (clients) {
			if (clients.size() != 0)
				return;
		}
		synchronized (hostClients) {
			hostClients.remove(hid);
		}
		if (callback != null)
			try {
				callback.onHostOffline(hid);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	protected ArrayList<String> getHostClient(String hostId) {
		if (hostId == null)
			return null;
		ArrayList<String> list = null;
		ArrayList<String> tmp = new ArrayList<String>();
		synchronized (hostClients) {
			list = hostClients.get(hostId);
		}
		if (list == null)
			return null;

		synchronized (list) {
			tmp.addAll(list);
		}
		return tmp;
	}

	// Client Info
	public void putClientInfo(String clientId, String key, String value) {
		if (clientId == null || key == null || value == null)
			return;
		Hashtable<String, String> values = null;
		synchronized (clientInfo) {
			values = clientInfo.get(clientId);
			if (values == null) {
				values = new Hashtable<>();
				clientInfo.put(clientId, values);
			}
		}
		synchronized (values) {
			values.put(key, value);
		}
	}

	protected String getClientInfo(String clientId, String key) {
		if (clientId == null || key == null)
			return null;
		Hashtable<String, String> values = null;
		synchronized (clientInfo) {
			values = clientInfo.get(clientId);
		}
		if (values == null)
			return null;
		synchronized (values) {
			return values.get(key);
		}
	}

	protected void delClientInfo(String clientId) {
		if (clientId == null || clientId.length() == 0)
			return;
		synchronized (clientInfo) {
			clientInfo.remove(clientId);
		}
	}

	protected void delClientinfo(String clientId, String key) {
		if (clientId == null || clientId.length() == 0)
			return;
		if (key == null || key.trim().length() == 0)
			return;
		Hashtable<String, String> values = null;
		synchronized (clientInfo) {
			values = clientInfo.get(clientId);
		}
		if (values == null)
			return;
		synchronized (values) {
			values.remove(key);
			if (values.size() != 0)
				return;
		}
		synchronized (clientInfo) {
			clientInfo.remove(clientId);
		}
	}

	protected void log(String method, String msg) {
		System.out.println(this.toString() + " :: " + method + " : " + msg);
	}
}
