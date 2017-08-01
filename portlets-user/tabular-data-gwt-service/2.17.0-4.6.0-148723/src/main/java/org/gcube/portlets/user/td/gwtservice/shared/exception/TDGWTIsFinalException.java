package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author Giancarlo Panichi
 *
 */
public class TDGWTIsFinalException extends TDGWTServiceException {

	private static final long serialVersionUID = 4306091799912937920L;

	/**
	 * 
	 */
	public TDGWTIsFinalException() {
		super();
	}

	/**
	 * @param message
	 *            Message
	 */
	public TDGWTIsFinalException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public TDGWTIsFinalException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
