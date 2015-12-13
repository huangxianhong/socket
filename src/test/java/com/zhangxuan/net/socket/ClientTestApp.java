package com.zhangxuan.net.socket;

import java.io.IOException;

import com.zhangxuan.net.socket.access.AsyncTcpClient;
import com.zhangxuan.net.socket.access.ClientCallback;
import com.zhangxuan.net.socket.access.Client;
import com.zhangxuan.net.socket.model.Frame;
import com.zhangxuan.net.socket.model.PingFrame;

public class ClientTestApp {

	public static void main(String[] args) throws IOException {
		ClientCallback cl = new ClientCallback() {

			@Override
			public void onConnected(Client client) {
				System.out.println();
			}

			@Override
			public void onDisconnected(Client client) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFrameReceived(Frame frame) throws Exception {
				if (frame instanceof PingFrame) {
					PingFrame pingFrame = (PingFrame) frame;
					int id = pingFrame.getId();
					System.out.println(id);
				} else {
					System.out.println("fffffffffffffff");
				}

			}

			@Override
			public void onFrameSent(String frameId) {
			}

		};

		AsyncTcpClient client = new AsyncTcpClient("192.168.31.100", 5555, cl);
		client.start();

		while (true) {
			try {
				System.out.print(".");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
