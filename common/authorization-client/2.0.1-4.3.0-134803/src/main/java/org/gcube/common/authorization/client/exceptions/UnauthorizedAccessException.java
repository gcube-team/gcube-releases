package org.gcube.common.authorization.client.exceptions;

public class UnauthorizedAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -950230930130868466L;

	protected UnauthorizedAccessException() {
		super();
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}

	
	
}
