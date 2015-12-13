package com.zhangxuan.net.socket.access;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Hashtable;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import com.zhangxuan.net.socket.model.Frame;
import com.zhangxuan.net.socket.model.PingFrame;

public class AsyncTcpClient implements Client {
	private final String tag = this.getClass().getSimpleName();
	private final Queue<Frame> sendFrames = new LinkedBlockingDeque<>();
	private final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	private final ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
	private final String id = UUID.randomUUID().toString();
	private final Hashtable<String, String> parameters = new Hashtable<>();

	private boolean isStoped = false;
	private boolean isWriting = false;
	private Object writingLock = new Object();

	private Frame currentSendFrame = null;
	private Frame currentReceiveFrame = null;

	private AsynchronousSocketChannel socket = null;
	private ClientCallback callback = null;

	private String host = null;
	private int port = 0;

	private static final CompletionHandler<Void, AsyncTcpClient> connectHandler = new CompletionHandler<Void, AsyncTcpClient>() {
		@Override
		public void completed(Void result, AsyncTcpClient client) {
			client.connected();
		}

		@Override
		public void failed(Throwable exc, AsyncTcpClient client) {
			client.stop();
		}
	};
	private static final CompletionHandler<Integer, AsyncTcpClient> readHandler = new CompletionHandler<Integer, AsyncTcpClient>() {

		@Override
		public void completed(Integer i, AsyncTcpClient client) {
			client.readed(i);
		}

		@Override
		public void failed(Throwable exc, AsyncTcpClient client) {
			client.stop();
		}
	};
	private static final CompletionHandler<Integer, AsyncTcpClient> writeHandler = new CompletionHandler<Integer, AsyncTcpClient>() {

		@Override
		public void completed(Integer i, AsyncTcpClient client) {
			client.writed(i);
		}

		@Override
		public void failed(Throwable exc, AsyncTcpClient client) {
			client.stop();
		}
	};

	@Override
	public String getId() {
		return id;
	}

	public AsyncTcpClient(String host, int port, ClientCallback clientListener) throws IOException {
		this(host, port, clientListener, null);
	}

	AsyncTcpClient(String host, int port, ClientCallback clientCallback, AsynchronousChannelGroup group) throws IOException {
		if (host == null)
			throw new IllegalArgumentException("host is null");
		if (port == 0)
			throw new IllegalArgumentException("port is 0");
		if (clientCallback == null)
			throw new IllegalArgumentException("clientListener is null");
		this.host = host;
		this.port = port;
		this.callback = clientCallback;
		socket = AsynchronousSocketChannel.open(group);
		writeBuffer.limit(0);
		readBuffer.limit(0);
	}

	AsyncTcpClient(AsynchronousSocketChannel socket, ClientCallback clientCallback) {
		if (socket == null)
			return;
		this.socket = socket;
		if (clientCallback == null)
			return;
		try {
			SocketAddress sa = socket.getRemoteAddress();
			if (sa instanceof InetSocketAddress) {
				InetSocketAddress isa = (InetSocketAddress) sa;
				this.host = isa.getHostName();
				this.port = isa.getPort();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.callback = clientCallback;
		writeBuffer.limit(0);
		readBuffer.limit(0);
		reading();
	}

	@Override
	public void send(Frame frame) {
		log("send", frame == null ? null : frame.toString());
		synchronized (sendFrames) {
			sendFrames.add(frame);
		}
		synchronized (writingLock) {
			if (isWriting)
				return;
		}
		writing();
	}

	@Override
	public void start() {
		if (isStoped) {
			log("start", "*** Can't Run again");
			return;
		}
		connecting();
	}

	@Override
	public void stop() {
		isStoped = true;
		if (callback != null)
			callback.onDisconnected(this);
		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
			} finally {
				socket = null;
			}
	}

	private void connecting() {
		log("connecting", "" + host + ":" + port);
		SocketAddress address = new InetSocketAddress(host, port);
		this.socket.connect(address, this, connectHandler);
	}

	void connected() {
		log("connected", "" + host + ":" + port);
		if (callback != null)
			callback.onConnected(this);
		reading();
	}

	private void reading() {
		if (socket == null)
			return;
		this.readBuffer.clear();
		socket.read(readBuffer, this, readHandler);
	}

	private void readed(int ri) {
		if (ri <= 0) {
			stop();
			return;
		}
		readBuffer.limit(ri);
		try {
			readFrame();
		} catch (FrameFormatException e) {
			log("readed", "*** " + e.toString());
			stop();
			return;
		} catch (IOException e) {
			log("readed", "*** " + e.toString());
			stop();
			return;
		}
		reading();
	}

	private void readFrame() throws IOException, FrameFormatException {
		readBuffer.flip();
		while (readBuffer.remaining() > 0) {
			if (currentReceiveFrame == null) {
				byte sign = readBuffer.get();
				currentReceiveFrame = Frame.createFrame(sign);
				if (currentReceiveFrame == null)
					throw new FrameFormatException();
				currentReceiveFrame.setSrc(this.getId());
				currentReceiveFrame.startToWrite();
			}
			int shouldRead = currentReceiveFrame.getWriteRemain();
			if (shouldRead > readBuffer.remaining())
				shouldRead = readBuffer.remaining();
			byte[] bs = new byte[shouldRead];
			readBuffer.get(bs);
			currentReceiveFrame.write(bs);
			if (currentReceiveFrame.getWriteRemain() == 0) {
				try {
					callback.onFrameReceived(currentReceiveFrame);
				} catch (Exception e) {
					log("readFrame", e.toString());
					// e.printStackTrace();
				}
				currentReceiveFrame = null;
			}
		}
	}

	private void writing() {
		isWriting = true;
		boolean ready = false;
		try {
			ready = writeReady();
		} catch (IOException e) {
			e.printStackTrace();
			stop();
			return;
		}

		if (!ready) {
			synchronized (writingLock) {
				isWriting = false;
			}
			return;
		}
		try {
			this.socket.write(this.writeBuffer, this, writeHandler);
		} catch (Exception e) {
			stop();
		}
	}

	private void writed(int wi) {
		if (wi <= 0) {
			stop();
			return;
		}
		writing();
	}

	private boolean writeReady() throws IOException {
		if (writeBuffer.remaining() > 0)
			return true;
		if (currentSendFrame != null) {
			byte[] bs = null;
			bs = currentSendFrame.read(1024);
			if (bs != null) {
				writeBuffer.clear();
				writeBuffer.put(bs);
				writeBuffer.limit(bs.length);
				writeBuffer.flip();
				return true;
			}
		}
		Frame frame = null;
		synchronized (sendFrames) {
			frame = sendFrames.poll();
		}
		if (frame == null) {
			return false;
		}
		frame.startToRead();
		currentSendFrame = frame;
		byte[] bs = null;
		bs = currentSendFrame.read(1023);
		if (bs != null) {
			writeBuffer.clear();
			writeBuffer.put(Frame.getSign(currentSendFrame));
			writeBuffer.put(bs);
			writeBuffer.limit(bs.length + 1);
			writeBuffer.flip();
			return true;
		}
		return false;
	}

	AsynchronousSocketChannel getSocket() {
		return socket;
	}

	private void log(String method, String msg) {
		System.out.println(tag + " :: " + method + " : " + msg);
	}

	int i = 0;

	@Override
	public void ping() {
		PingFrame f = new PingFrame();
		f.setId(++i);
		send(f);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void setParameter(String key, String value) {
		synchronized (parameters) {
			parameters.put(key, value);
		}
	}

	@Override
	public String getParameter(String key) {
		synchronized (parameters) {
			return parameters.get(key);
		}
	}

}