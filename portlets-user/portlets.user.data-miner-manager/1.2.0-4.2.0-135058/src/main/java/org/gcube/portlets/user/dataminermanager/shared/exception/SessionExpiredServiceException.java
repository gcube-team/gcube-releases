package org.gcube.portlets.user.dataminermanager.shared.exception;

/**
 * Session Expired Service Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class SessionExpiredServiceException extends
		ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public SessionExpiredServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public SessionExpiredServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param t
	 */
	public SessionExpiredServiceException(String message, Throwable t) {
		super(message, t);
	}

}
