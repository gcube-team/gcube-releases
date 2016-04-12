package org.gcube.resources.discovery.client.api;


/**
 * Raised when services endpoints cannot be discovered.
 * 
 * @author Fabio Simeoni
 *
 */
public class DiscoveryException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance with a message.
	 * @param msg the message
	 */
	public DiscoveryException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance from a cause.
	 * @param cause the cause
	 */
	public DiscoveryException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an instance from a message and a cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public DiscoveryException(String msg,Throwable cause) {
		super(msg,cause);
	}
	

}
