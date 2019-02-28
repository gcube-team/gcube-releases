package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.exception;

/**
 * Session Expired Service Exception
 * 
 * @author Giancarlo Panichi
 *
 */
public class SessionExpiredServiceException extends ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public SessionExpiredServiceException() {
		super();
	}

	/**
	 * 
	 * @param message
	 *            message
	 */
	public SessionExpiredServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            message
	 * @param throwable
	 *            errror
	 */
	public SessionExpiredServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
