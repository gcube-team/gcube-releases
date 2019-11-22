package org.gcube.portlets.user.td.expressionwidget.shared.exception;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ExpressionParserException extends Exception {

	private static final long serialVersionUID = 8823457309498708839L;

	public ExpressionParserException() {
		super();
	}

	/**
	 * 
	 * @param message
	 *            Message
	 */
	public ExpressionParserException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public ExpressionParserException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
