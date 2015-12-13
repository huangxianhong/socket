package com.zhangxuan.net.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.zhangxuan.net.socket.access.Client;
import com.zhangxuan.net.socket.convergence.Communication;
import com.zhangxuan.net.socket.convergence.CommunicationCallback;
import com.zhangxuan.net.socket.convergence.Config;
import com.zhangxuan.net.socket.convergence.StandardCommunication;
import com.zhangxuan.net.socket.model.Cmd;
import com.zhangxuan.net.socket.model.Frame;
import com.zhangxuan.net.socket.model.TextFrame;

public class CommunicationTestApp {
	static Communication communication = null;
	static Config config = getConfig("31Office");
	private static CommunicationCallback listener = new CommunicationCallback() {

		@Override
		public void onHostOnline(String hostId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHostOffline(String hostId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFrameSent(Frame frame) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFrameReceived(Frame frame) {
			if (frame instanceof TextFrame) {
				try {
					String text = ((TextFrame) frame).getText();
					System.out.println(text);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onConnectionDisconnected(Client client) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectionConnected(Client client) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectionAccepted(Client client) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCmdSent(String id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCmdReceived(Cmd cmd) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStarted(String id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStoped(String id) {
			// TODO Auto-generated method stub

		}
	};

	public static Config getConfig(String configname) {
		if (configname == null)
			throw new IllegalArgumentException("config name is null");
		if (!configname.endsWith(".config"))
			configname += ".config";
		Config config = Config.load(configname);
		return config;
	}

	public static void main(String[] args) throws IOException, Exception {
		communication = new StandardCommunication(config, listener);
		communication.start();
		commandMode();
	}

	static void commandMode() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String line = br.readLine().trim();
				System.out.println("<CMD>  " + line);
				String[] ss = line.split(" ");
				if (ss.length <= 0)
					continue;
				executeLine(ss);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void executeLine(String[] args) {
		String cmd = args[0];
		switch (cmd) {
		case "connect":
			if (args.length != 2) {
				System.out.println("help : connect (hostid)");
				return;
			}
			try {
				communication.connect(args[1]);
			} catch (Exception e) {
				System.out.println("*** " + e.toString());
			}
			break;
		case "connects":
			if (args.length != 3) {
				System.out.println("help : connects (hostid) (count)");
				return;
			}
			int cnt = Integer.valueOf(args[2]);
			for (int i = 0; i < cnt; i++)
				try {
					communication.connect(args[1]);
				} catch (Exception e) {
					System.out.println("*** " + e.toString());
				}
			break;
		case "sendtext":
			if (args.length != 3) {
				System.out.println("help : sendtext (hostid) (text)");
				return;
			}
			TextFrame frame = new TextFrame();
			frame.setData(args[2].getBytes());
			try {
				communication.send(args[1], frame);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			break;
		case "sendtexts":
			if (args.length != 4) {
				System.out.println("help : sendtext (hostid) (text) (count)");
				return;
			}
			int count = 0;
			try {
				count = Integer.valueOf(args[3]);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			try {
				for (int i = 0; i < count; i++) {
					TextFrame frame1 = new TextFrame();
					frame1.setData((args[2] + " - " + i).getBytes());
					communication.send(args[1], frame1);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			break;
		case "senddata":
			break;
		case "printhosts":
			break;
		default:
			break;
		}
	}
}