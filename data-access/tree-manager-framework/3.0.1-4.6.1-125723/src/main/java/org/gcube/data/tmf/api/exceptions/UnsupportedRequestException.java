package org.gcube.data.tmf.api.exceptions;


/**
 * Raised with requests to services which are not supported by the target plugin.
 * 
 * @author Fabio Simeoni
 *
 */
public class UnsupportedRequestException extends InvalidRequestException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 */
	public UnsupportedRequestException() {}
	
	/**
	 * Creates an instance with a message.
	 * @param msg the message
	 */
	public UnsupportedRequestException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a message and a cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public UnsupportedRequestException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	/**
	 * Creates an instance with a cause.
	 * @param cause the cause
	 */
	public UnsupportedRequestException(Throwable cause) {
		super(cause);
	}
}
