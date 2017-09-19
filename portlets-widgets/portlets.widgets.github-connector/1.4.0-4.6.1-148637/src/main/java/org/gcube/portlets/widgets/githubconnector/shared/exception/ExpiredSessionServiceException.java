package org.gcube.portlets.widgets.githubconnector.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ExpiredSessionServiceException extends ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public ExpiredSessionServiceException() {
		super();
	}

	/**
	 * @param message
	 *            message
	 */
	public ExpiredSessionServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            message
	 * @param throwable
	 *            throwable
	 */
	public ExpiredSessionServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
