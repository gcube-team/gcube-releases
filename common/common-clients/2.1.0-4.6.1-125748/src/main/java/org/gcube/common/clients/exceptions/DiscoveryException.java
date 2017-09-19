package org.gcube.common.clients.exceptions;


/**
 * Raised when services endpoints cannot be discovered.
 * 
 * @author Fabio Simeoni
 *
 */
public class DiscoveryException extends ServiceException {


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
	

}
