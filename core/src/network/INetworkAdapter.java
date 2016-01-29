package network;

import java.util.List;


public interface INetworkAdapter {
	public void sendMessage(String message, boolean force);
	
	public void subscribeListener(INetworkAdapterListener listener, String tag) throws NetworkException;
	
	public interface INetworkAdapterListener {
		void messageReceived(List<String> contents, String userCode) throws NetworkException;
	}
}
