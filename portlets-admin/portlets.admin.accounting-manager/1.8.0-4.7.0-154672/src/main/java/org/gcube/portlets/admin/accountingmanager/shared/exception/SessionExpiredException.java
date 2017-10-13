package org.gcube.portlets.admin.accountingmanager.shared.exception;

/**
 *
 * 
 * @author Giancarlo Panichi
 *
 */
public class SessionExpiredException extends ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public SessionExpiredException() {
		super();
	}

	/**
	 * @param message
	 *            message
	 */
	public SessionExpiredException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            message
	 * @param throwable
	 *            exception
	 */
	public SessionExpiredException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
