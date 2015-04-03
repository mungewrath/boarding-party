package network;

import java.io.BufferedReader;

public interface INetworkAdapter {
	public void sendMessage(String message, boolean force);
	
	public void subscribeListener(INetworkAdapterListener listener, String tag) throws NetworkException;
	
	public interface INetworkAdapterListener {
		void messageReceived(BufferedReader in, String userCode) throws NetworkException;
	}
}
