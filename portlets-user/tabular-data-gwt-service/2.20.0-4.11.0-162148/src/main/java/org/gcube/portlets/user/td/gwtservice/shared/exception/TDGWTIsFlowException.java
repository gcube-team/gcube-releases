package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author Giancarlo Panichi
 *
 */
public class TDGWTIsFlowException extends TDGWTServiceException {

	private static final long serialVersionUID = 4306091799912937920L;

	/**
	 * 
	 */
	public TDGWTIsFlowException() {
		super();
	}

	/**
	 * @param message
	 *            Message
	 */
	public TDGWTIsFlowException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public TDGWTIsFlowException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
