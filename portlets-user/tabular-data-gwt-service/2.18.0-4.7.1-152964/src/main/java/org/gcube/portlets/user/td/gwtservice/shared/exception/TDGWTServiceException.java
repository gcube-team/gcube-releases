/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TDGWTServiceException extends Exception {

	private static final long serialVersionUID = -9066034060104406559L;

	/**
	 * 
	 */
	public TDGWTServiceException() {
		super();
	}

	/**
	 * @param message
	 *            Message
	 */
	public TDGWTServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public TDGWTServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
