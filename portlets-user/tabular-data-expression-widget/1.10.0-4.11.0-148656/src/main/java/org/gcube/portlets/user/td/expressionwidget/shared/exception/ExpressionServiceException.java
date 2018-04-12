package org.gcube.portlets.user.td.expressionwidget.shared.exception;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ExpressionServiceException extends Exception {

	private static final long serialVersionUID = -9066034060104406559L;

	/**
	 * 
	 */
	public ExpressionServiceException() {
		super();
	}

	/**
	 * @param message
	 *            Message
	 */
	public ExpressionServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public ExpressionServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
