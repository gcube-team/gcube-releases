package org.gcube.data.spd.model.exceptions;

public class InvalidQueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidQueryException() {
		super();
	}

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public InvalidQueryException(String message) {
		super(message);
	}

	public InvalidQueryException(Throwable cause) {
		super(cause);
	}

	
	
}
