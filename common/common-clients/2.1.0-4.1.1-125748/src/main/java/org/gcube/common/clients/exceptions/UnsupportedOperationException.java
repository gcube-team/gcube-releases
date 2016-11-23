package org.gcube.common.clients.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

/**
 * Raised with requests to service operations that are not supported.
 * 
 * @author Fabio Simeoni
 *
 */
@Unrecoverable
public class UnsupportedOperationException extends InvalidRequestException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 */
	public UnsupportedOperationException() {}
	
	/**
	 * Creates an instance with a message.
	 * @param msg the message
	 */
	public UnsupportedOperationException(String msg) {
		super(msg);
	}
}
