package com.zhangxuan.net.socket.model;

public class CmdFrame extends BinaryFrame {
	public void setCmd(Cmd cmd) {
		String str = CmdUtil.toJsonString(cmd);
		setData(str.getBytes());
	}

	public Cmd getCmd() {
		byte bs[] = getData();
		if (bs == null)
			return null;
		String str = new String(bs);
		return CmdUtil.fromJsonString(str);
	}
}
