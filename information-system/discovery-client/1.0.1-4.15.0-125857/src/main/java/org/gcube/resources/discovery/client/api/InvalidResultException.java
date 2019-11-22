package org.gcube.resources.discovery.client.api;

/**
 * Raised by {@link DiscoveryClient}s for result parsing errors.
 * 
 * @author Fabio Simeoni
 *
 */
public class InvalidResultException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance with a given message.
	 * @param msg the message
	 */
	public InvalidResultException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public InvalidResultException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an instance with a given message and a given cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public InvalidResultException(String msg, Throwable cause) {
		super(msg,cause);
	}
}
