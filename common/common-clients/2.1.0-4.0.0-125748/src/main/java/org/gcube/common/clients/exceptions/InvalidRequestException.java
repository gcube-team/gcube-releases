package org.gcube.common.clients.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

/**
 * A {@link ServiceException} raised when client requests are invalid for services.
 * 
 * @author Fabio Simeoni
 *
 */
@Unrecoverable
public class InvalidRequestException extends ServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 */
	public InvalidRequestException(){
		super();
	}
	
	/**
	 * Creates an instance with a given message.
	 * @param msg the message
	 */
	public InvalidRequestException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance from an underlying cause.
	 * 
	 * @param cause the cause
	 */
	public InvalidRequestException(Throwable cause) {
		super(cause);
	}

	 /** Creates an instance with a given message and an underlying cause.
	 * 
	 * @param msg the message
	 * @param cause the cause
	 */
	public InvalidRequestException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
