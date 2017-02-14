package gr.cite.bluebridge.analytics.discovery.exceptions;

public class ServiceDiscoveryException extends Exception {

	private static final long serialVersionUID = 3265120262579560144L;

	public ServiceDiscoveryException(String Message){
		super(Message);
	}

	public ServiceDiscoveryException(String Message,Throwable cause){
		super(Message,cause);
	}
}
