package com.zhangxuan.net.socket;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.zhangxuan.net.socket.access.FrameFormatException;
import com.zhangxuan.net.socket.model.BinaryFrame;
import com.zhangxuan.net.socket.model.PingFrame;

public class FrameTestApp {
	public static void main(String[] args) throws IOException, FrameFormatException {
		// readWholeBinaryFrame();
		// readWholePingFrame();
		// readPingFrameLimit();
		// readBinaryFrameLimit();
		// writeWholePingFrame();
		// writeLimitPingFrame();
		// writeWholeBinaryFrame();
		writeWholeBinaryFrame();
	}

	static void readWholeBinaryFrame() throws IOException {
		System.out.println("readWholeBinaryFrame");
		BinaryFrame frame = new BinaryFrame();
		frame.setId(1);
		frame.setData("HelloWorld".getBytes());
		frame.startToRead();
		byte bs[] = frame.read(1024);
		printBytes(bs);
	}

	static void readWholePingFrame() throws IOException {
		System.out.println("readWholePingFrame");
		PingFrame frame = new PingFrame();
		frame.setId(1);
		frame.startToRead();
		byte bs[] = frame.read(1024);
		printBytes(bs);
	}

	static void readPingFrameLimit() throws IOException {
		System.out.println("readPingFrameLimit");
		PingFrame frame = new PingFrame();
		frame.setId(1);
		frame.startToRead();
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.put(frame.read(2));
		bf.put(frame.read(3));
		bf.flip();
		byte[] bs = new byte[bf.remaining()];
		bf.get(bs);
		printBytes(bs);
	}

	static void readBinaryFrameLimit() throws IOException {
		System.out.println("readBinaryFrameLimit");
		BinaryFrame frame = new BinaryFrame();
		frame.setId(1);
		frame.setData("How do you do?".getBytes());
		frame.startToRead();
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.put(frame.read(2));
		bf.put(frame.read(3));
		bf.put(frame.read(5));
		bf.put(frame.read(3));
		bf.put(frame.read(10));
		bf.flip();
		byte[] bs = new byte[bf.remaining()];
		bf.get(bs);
		printBytes(bs);
	}

	static void writeWholePingFrame() throws IOException, FrameFormatException {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.putInt(255);
		bf.flip();
		byte[] bs = new byte[4];
		bf.get(bs);
		PingFrame frame = new PingFrame();
		frame.startToWrite();
		frame.write(bs);
		System.out.println(frame.getId());
	}

	static void writeLimitPingFrame() throws IOException, FrameFormatException {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.putInt(255);
		bf.putInt(2);
		bf.flip();
		PingFrame frame = new PingFrame();
		frame.startToWrite();
		byte[] bs = new byte[2];
		bf.get(bs);
		frame.write(bs);
		bs = new byte[3];
		bf.get(bs);
		frame.write(bs);
		System.out.println(frame.getId());
	}

	static void writeWholeBinaryFrame() throws IOException, FrameFormatException {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.putInt(255);
		bf.putInt(5);
		bf.put("Hello".getBytes());
		bf.flip();
		BinaryFrame frame = new BinaryFrame();
		frame.startToWrite();
		byte[] bs = new byte[8];
		bf.get(bs);
		frame.write(bs);
		bs = new byte[bf.remaining()];
		bf.get(bs);
		frame.write(bs);
		System.out.println(frame.getId());
		System.out.println(new String(frame.getData()));
	}

	static void writeLimitBinaryFrame() throws IOException, FrameFormatException {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.putInt(255);
		bf.putInt(17);
		bf.put("Hello,how are you".getBytes());
		bf.flip();
		BinaryFrame frame = new BinaryFrame();
		frame.startToWrite();
		byte[] bs = new byte[1];
		bf.get(bs);
		frame.write(bs);
		bs = new byte[5];
		bf.get(bs);
		frame.write(bs);
		bs = new byte[2];
		bf.get(bs);
		frame.write(bs);
		bs = new byte[bf.remaining()];
		bf.get(bs);
		frame.write(bs);
		System.out.println(frame.getId());
		System.out.println(frame.getDataLength());
		System.out.println(new String(frame.getData()));
	}

	public static void printBytes(byte[] bs) {
		StringBuffer sb = new StringBuffer();
		sb.append("======= =======================HEX======================\t======TEXT======\n");
		if (bs == null) {
			sb.append("\tNULL\n");
			sb.append("\t================================================\t================\n");
			return;
		}
		sb.append("INDEX\t 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  \t0123456789ABCDEF\n");
		sb.append("------- ------------------------------------------------\t----------------\n");
		int c = 0;
		int i = 0;
		byte[] line = new byte[16];
		for (byte b : bs) {
			if (i == 0) {
				sb.append(c / 16 + 1);
				sb.append("\t");
			}
			c++;
			int v = b & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() == 0)
				hv = "--";
			else if (hv.length() == 1)
				hv = "0" + hv;
			sb.append(hv.toUpperCase() + " ");
			line[i] = b;
			i++;
			if (c % 16 == 0) {
				String ln = new String(line);
				ln = ln.replace("\n", ".").replace("\r", ".");
				sb.append("\t" + ln);
				i = 0;
				sb.append("\n");
			}
		}
		int left = 16 - i;
		for (int n = 0; n < left; n++)
			sb.append("   ");
		String ln = new String(line, 0, i);
		ln = ln.replace("\n", ".").replace("\r", ".");
		sb.append(" \t" + ln);
		sb.append("\n======= ================================================\t================\n");
		sb.append("\tTotal Length : " + bs.length + "\n");
		System.out.println(sb.toString());
	}
}
