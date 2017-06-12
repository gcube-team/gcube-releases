package org.gcube.common.clients.exceptions;

/**
 * An exceptions that occurs in the attempt to communicate with service endpoints.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 */
	public ServiceException() {}
	
	/**
	 * Creates an instance with a given message.
	 * 
	 * @param msg the message
	 */
	public ServiceException(String msg) {
		super(msg);
	}

	/**
	 * Creates an instance from an underlying cause.
	 * 
	 * @param cause the cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

	 /** Creates an instance with a given message and an underlying cause.
	 * 
	 * @param msg the message
	 * @param cause the cause
	 */
	public ServiceException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
