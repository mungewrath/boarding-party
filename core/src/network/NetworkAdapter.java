package network;

import java.util.HashMap;
import java.util.Map;

import network.NetworkException.NetworkErrorCode;

public class NetworkAdapter implements INetworkAdapter {
	private INetworkAdapterListener masterNotifier;
	private Map<String, INetworkAdapterListener> listeners = new HashMap<String, INetworkAdapterListener>();
	

	@Override
	public void sendMessage(String message, boolean force) {
		// TODO Auto-generated method stub

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
