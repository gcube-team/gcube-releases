/**
 * 
 */
package org.gcube.common.homelibrary.home.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class InternalErrorException extends Exception {

	private static final long serialVersionUID = -3709604100572065057L;

	/**
	 * Create a new internal error exception.
	 * @param cause the exception cause.
	 */
	public InternalErrorException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new internal error exception.
	 * @param message the exception message.
	 */
	public InternalErrorException(String message) {
		super(message);
	}

	/**
	 * Create a new internal error exception.
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public InternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}
