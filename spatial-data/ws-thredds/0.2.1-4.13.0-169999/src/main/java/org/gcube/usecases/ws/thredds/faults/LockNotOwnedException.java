package org.gcube.usecases.ws.thredds.faults;

public class LockNotOwnedException extends InternalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 96212116766290884L;

	public LockNotOwnedException() {
		// TODO Auto-generated constructor stub
	}

	public LockNotOwnedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public LockNotOwnedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public LockNotOwnedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public LockNotOwnedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
