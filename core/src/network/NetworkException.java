package network;

public class NetworkException extends Exception {
	public NetworkException(String message, NetworkErrorCode code) {
		super(message);
	}
	
	public enum NetworkErrorCode {
		NERR_ADAPTER_TAG_TAKEN,
		NERR_NETWORK_ERROR,
		NERR_RECEIVER_REJECTED_MESSAGE
	}
}
