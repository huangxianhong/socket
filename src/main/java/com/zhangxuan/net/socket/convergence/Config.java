package com.zhangxuan.net.socket.convergence;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.zhangxuan.net.socket.util.IoUtils;

public class Config implements Serializable {

	private static final long serialVersionUID = 1L;
	private final HostInfo info = new HostInfo();
	private final Hashtable<String, HostInfo> hosts = new Hashtable<>();
	private final ArrayList<String> trustHostAddresses = new ArrayList<>();

	public HostInfo getInfo() {
		return info;
	}

	public void setInfo(HostInfo info) {
		synchronized (info) {
			this.info.setId(info.getId());
			this.info.setHost(info.getHost());
			this.info.setPort(info.getPort());
			this.info.setSecurityCode(info.getSecurityCode());
		}
	}

	public Hashtable<String, HostInfo> getHosts() {
		if (hosts == null)
			return null;
		Hashtable<String, HostInfo> tmp = new Hashtable<String, HostInfo>();
		synchronized (hosts) {
			tmp.putAll(hosts);
		}
		return hosts;
	}

	public void setHosts(Hashtable<String, HostInfo> hosts) {
		synchronized (hosts) {
			this.hosts.clear();
			this.hosts.putAll(hosts);
		}
	}

	public void addHosts(HostInfo host) {
		synchronized (hosts) {
			hosts.put(host.getId(), host);
		}
	}

	public ArrayList<String> getTrustHostAddresses() {
		ArrayList<String> tmp = new ArrayList<String>();
		synchronized (trustHostAddresses) {
			tmp.addAll(trustHostAddresses);
		}
		return tmp;
	}

	public void setTrustHostAddresses(ArrayList<String> list) {
		if (list == null)
			return;
		synchronized (trustHostAddresses) {
			trustHostAddresses.clear();
			trustHostAddresses.addAll(list);
		}
	}

	public void addTrustHostAddress(String address) {
		if (address == null)
			return;
		synchronized (trustHostAddresses) {
			trustHostAddresses.add(address);
		}
	}

	public static Config load(String configFileName) {
		String text = null;
		ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
		Config config = null;
		InputStream inStream = null;
		try {
			if (standardClassloader != null) {
				inStream = standardClassloader.getResourceAsStream(configFileName);
			}
			if (inStream == null) {
				inStream = Config.class.getClass().getResourceAsStream(configFileName);
			}
			if (inStream != null) {
				text = IoUtils.readString(inStream);
				Gson gson = new Gson();
				config = gson.fromJson(text, Config.class);
			}
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null)
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	// public static Config load(String configFileName) {
	// String configString = null;
	// FileInputStream fis = null;
	// File file = new File(configFileName);
	// if (!file.exists())
	// return null;
	// Long length = file.length();
	// if (length == 0)
	// return null;
	// try {
	// fis = new FileInputStream(file);
	// byte[] bs = new byte[length.intValue()];
	// fis.read(bs);
	// configString = new String(bs);
	// } catch (Exception e) {
	// return null;
	// } finally {
	// if (fis != null)
	// try {
	// fis.close();
	// } catch (Exception e2) {
	// } finally {
	// fis = null;
	// }
	// }
	// if (configString.length() == 0)
	// return null;
	// Gson gson = new Gson();
	// Config config = null;
	// try {
	// config = gson.fromJson(configString, Config.class);
	// return config;
	// } catch (Exception e) {
	// }
	// return null;
	// }

	public static void main(String[] args) {
		Config config = new Config();
		HostInfo myinfo = new HostInfo();
		config.setInfo(myinfo);
		Hashtable<String, HostInfo> hosts = new Hashtable<>();
		config.setHosts(hosts);
		ArrayList<String> trustedHosts = new ArrayList<>();
		config.setTrustHostAddresses(trustedHosts);

		myinfo.setId("huanglw");
		myinfo.setHost("192.168.168.55");
		myinfo.setPort(5555);
		myinfo.setSecurityCode("HUANGLW");

		HostInfo h1 = new HostInfo("hxh", "192.168.168.94", 5555, "HUANGXIANHONG");
		HostInfo h2 = new HostInfo("lq", "192.168.168.91", 5555, "LONGQING");
		hosts.put(h1.getId(), h1);
		hosts.put(h2.getId(), h2);

		trustedHosts.add("192.168.168.");
		trustedHosts.add("111.111.");

		Gson gson = new Gson();
		System.out.println(gson.toJson(config));
	}
}
