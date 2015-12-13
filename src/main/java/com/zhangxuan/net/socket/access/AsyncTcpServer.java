package com.zhangxuan.net.socket.access;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zhangxuan.net.socket.model.Frame;

public class AsyncTcpServer extends AbstractServer {

	private Thread mainThread = new Thread(this);
	private volatile boolean isStoped = false;
	private ExecutorService executor = Executors.newFixedThreadPool(30);
	private AsynchronousChannelGroup asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);;
	private AsynchronousServerSocketChannel serverSocketChannel = null;
	private static final CompletionHandler<AsynchronousSocketChannel, AsyncTcpServer> acceptHandler = new CompletionHandler<AsynchronousSocketChannel, AsyncTcpServer>() {

		@Override
		public void completed(AsynchronousSocketChannel socket, AsyncTcpServer asyncTcpServer) {
			asyncTcpServer.accepted(socket);
		}

		@Override
		public void failed(Throwable e, AsyncTcpServer attachment) {
			e.printStackTrace();
		}
	};

	private String host = null;
	private int port = 0;
	private long acceptedCount = 0;

	public AsyncTcpServer(String host, int port, ServerCallback callback) throws IOException, Exception {
		if (host == null || host.length() == 0)
			host = "0.0.0.0";
		if (port <= 0)
			throw new IllegalArgumentException("unknown port");
		this.host = host;
		this.port = port;
		setCallback(callback);
	}

	@Override
	public void run() {
		if (isStoped) {
			log("run", "*** Can't run twice");
			return;
		}
		log("run", "Running ...");
		try {
			log("run", "Starting Server ...");
			serverSocketChannel = AsynchronousServerSocketChannel.open(asyncChannelGroup);
			for (SocketOption<?> so : serverSocketChannel.supportedOptions())
				System.out.println(so.name() + " = " + serverSocketChannel.getOption(so));
			InetSocketAddress serverAddress = new InetSocketAddress(host, port);
			serverSocketChannel.bind(serverAddress);
			log("run", "Server initialize done.");
		} catch (IOException e) {
			e.printStackTrace();
			log("run", "Server initialize error : " + e.toString());
			return;
		}
		ServerCallback callback = getCallback();
		if (callback != null)
			callback.onStarted(this);
		accepting();
		log("run", "Server ready ...");
		while (true) {
			// Hashtable<String, IClient> clients = getClients();
			// for (IClient client : clients.values()) {
			// log("run", "Ping client " + client.getId());
			// client.ping();
			// }
			log("run", "\t--- Connections:" + getClients().size() + " \t--- Accepted:" + acceptedCount);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log("run", "Interrupting ...");
				break;
			}
		}
		log("run", "Shutdown Thread Pool ...");
		executor.shutdownNow();
		log("run", "Exiting ...");
		isStoped = true;
		if (callback != null)
			callback.onStarted(this);
	}

	@Override
	public void send(Frame frame) throws FrameTargetException {
		if (frame == null)
			return;
		String dst = frame.getDst();
		if (dst == null)
			throw new FrameTargetException("dst is null");
		Client client = findClient(dst);
		if (client == null)
			throw new FrameTargetException("dst client can't be found : dst clientid=" + dst);
		client.send(frame);
	}

	@Override
	public void start() {
		mainThread.start();
	}

	@Override
	public void stop() {
		mainThread.interrupt();
	}

	private void accepting() {
		serverSocketChannel.accept(this, acceptHandler);
	}

	private void accepted(AsynchronousSocketChannel socket) {
		if (socket == null)
			return;
		acceptedCount++;
		String remoteAddress = null;
		try {
			remoteAddress = socket.getRemoteAddress().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("acceptFinished", "Accepted : " + remoteAddress);
		AsyncTcpClient client = new AsyncTcpClient(socket, this);
		register(client);
		ServerCallback listener = getCallback();
		if (listener != null)
			listener.onAccepted(client);
		accepting();
	}

	@Override
	public Client connect(String host, int port) throws IOException {
		log("connect", "host=" + host + "\tport=" + port);
		AsyncTcpClient client = new AsyncTcpClient(host, port, this, asyncChannelGroup);
		client.start();
		return client;
	}

	@Override
	public void close(String clientId) {
		Client client = findClient(clientId);
		if (client == null)
			return;
		client.stop();
	}

}
