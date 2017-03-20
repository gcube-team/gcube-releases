package org.gcube.vomanagement.vomsapi.impl;

/**
 * A {@link CredentialsRetrievalException} is thrown when something goes wrong
 * retrieving credentials from a MyProxy service.
 * 
 * @author Paolo Roccetti
 */
public class CredentialsRetrievalException extends Exception {

	/**
	 * Constructor
	 */
	public CredentialsRetrievalException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 */
	public CredentialsRetrievalException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the exception cause
	 */
	public CredentialsRetrievalException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public CredentialsRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

}
