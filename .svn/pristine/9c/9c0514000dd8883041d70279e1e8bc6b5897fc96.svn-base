package com.zhangxuan.net.socket.model;

import com.google.gson.Gson;

public class CmdUtil {

	public static String toJsonString(Cmd cmd) {
		Gson gson = new Gson();
		return gson.toJson(cmd);
	}

	public static Cmd fromJsonString(String jsonString) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, Cmd.class);
	}

	public static Cmd createHello(String srcHost, String srcCode, String dstHost, String dstCode) {
		Cmd cmd = new Cmd("Hello");
		cmd.setParameter("srcHost", srcHost);
		cmd.setParameter("srcCode", srcCode);
		cmd.setParameter("dstHost", dstHost);
		cmd.setParameter("dstCode", dstCode);
		return cmd;
	}

	public static Cmd createHi() {
		Cmd cmd = new Cmd("Hi");
		return cmd;
	}
}
