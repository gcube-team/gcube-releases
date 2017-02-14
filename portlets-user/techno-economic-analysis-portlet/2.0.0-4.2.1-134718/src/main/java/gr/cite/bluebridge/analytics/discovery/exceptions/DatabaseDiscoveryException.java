package gr.cite.bluebridge.analytics.discovery.exceptions;

public class DatabaseDiscoveryException extends Exception {
	private static final long serialVersionUID = -702769990521199881L;

	public DatabaseDiscoveryException(String Message){
		super(Message);
	}

	public DatabaseDiscoveryException(String Message,Throwable cause){
		super(Message,cause);
	}
}
