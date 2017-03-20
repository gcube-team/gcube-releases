package org.gcube.vomanagement.vomsapi.impl;

/**
 * A {@link VOMSAdminException} is thrown when a problem occurs interacting with
 * the administrative interface of a VOMS service.
 * 
 * @author Paolo Roccetti
 */
public class VOMSAdminException extends Exception {

	/**
	 * Constructor
	 */
	public VOMSAdminException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 */
	public VOMSAdminException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the exception cause
	 */
	public VOMSAdminException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public VOMSAdminException(String message, Throwable cause) {
		super(message, cause);
	}

}
