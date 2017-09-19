package org.gcube.portlets.user.td.expressionwidget.shared.exception;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ConditionTypeMapException extends Exception {

	private static final long serialVersionUID = -9066034060104406559L;

	/**
	 * 
	 */
	public ConditionTypeMapException() {
		super();
	}

	/**
	 * 
	 * @param message
	 *            Message
	 */
	public ConditionTypeMapException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Error
	 */
	public ConditionTypeMapException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
