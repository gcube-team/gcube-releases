package org.gcube.common.vremanagement.deployer.impl.operators.common;

@SuppressWarnings("serial")
public class UpdateException extends Exception {

	/**
	 * Creates a new Deploy Exception
	 */
	public UpdateException() {		
	}

	/**
	 * @param message the error message
	 */
	public UpdateException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause of the exception
	 */
	public UpdateException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the error message
	 * @param cause the cause of the exception
	 */
	public UpdateException(String message, Throwable cause) {
		super(message, cause);
	}

}

