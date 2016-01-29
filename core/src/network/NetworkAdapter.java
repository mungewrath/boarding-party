package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import network.NetworkException.NetworkErrorCode;

public class NetworkAdapter implements INetworkAdapter {
	private INetworkAdapterListener masterNotifier;
	private Map<String, INetworkAdapterListener> listeners = new HashMap<String, INetworkAdapterListener>();
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private static final String MESSAGE_DELIMITER = "<eom>";
	
	public NetworkAdapter(Socket socket) throws IOException {
		this.socket = socket;
		writer = new PrintWriter(socket.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void beginListening() {
		ReceiverThread thread = new ReceiverThread();
	}
	
	protected class ReceiverThread implements Runnable {
		private Thread t;
		
		public ReceiverThread() {
			t = new Thread(this, "Network receiver thread"); 
			t.start();
		}

		@Override
		public void run() {
			while(!socket.isClosed()) {
				try {
					List<String> contents = new ArrayList<>();
					String user = reader.readLine();
					System.out.println("Read 'user':"+user);
					if(user == null) {
						Thread.sleep(250);
						continue;
					}
					String line = reader.readLine();
					while(!line.equals(MESSAGE_DELIMITER)) {
						contents.add(line);
						line = reader.readLine();
					}
					if(user.equals("")) {
						masterNotifier.messageReceived(contents, user);
					} else if(listeners.containsKey(user)) {
						listeners.get(user).messageReceived(contents, user);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NetworkException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Socket closed.");
		}
	}
	
	protected class SenderThread implements Runnable {
		private Thread t;
		private String message;
		
		public SenderThread(String message) {
			this.message = message;
			t = new Thread(this, "Network sender thread");
			t.start();
		}

		@Override
		public void run() {
			synchronized(NetworkAdapter.this) {
				System.out.println("Sending message:" + message);
				writer.print(message);
				writer.flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void sendMessage(String message, boolean force) {
		if(message.contains(MESSAGE_DELIMITER)) {
			throw new IllegalArgumentException("Messages must not contain the sequence " + MESSAGE_DELIMITER);
		}
		SenderThread thread = new SenderThread("placeholder\n" + message + MESSAGE_DELIMITER + "\n");
	}

	@Override
	public void subscribeListener(INetworkAdapterListener listener, String tag) throws NetworkException {
		if(!listeners.containsKey(tag)) {
			listeners.put(tag, listener);
		} else {
			throw new NetworkException("Error adding subscriber: tag " + tag + " already taken", NetworkErrorCode.NERR_ADAPTER_TAG_TAKEN);
		}
	}

}
