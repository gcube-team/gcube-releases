package org.gcube.common.clients.exceptions;

/**
 * A {@link ServiceException} raised when services endpoints are not reachable.  
 * 
 * @author Fabio Simeoni
 *
 */
public class NoSuchEndpointException extends ServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance from an underlying cause.
	 * @param cause the cause
	 */
	public NoSuchEndpointException(Throwable cause) {
		super(cause);
	}
}
