package com.zhangxuan.net.socket.model;

import java.util.HashMap;

public class Cmd {
	private String name = null;
	private HashMap<String, String> parameter = new HashMap<>();

	public Cmd() {
	}

	public Cmd(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParameter(String key, String value) {
		if (key == null)
			return;
		parameter.put(key, value);
	}

	public String getParameter(String key) {
		if (key == null || !parameter.containsKey(key))
			return null;
		return parameter.get(key);
	}

	public void delParameter(String key) {
		parameter.remove(key);
	}
}
