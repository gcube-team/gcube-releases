package org.gcube.data.analysis.tabulardata.exceptions;

public class TabularResourceLockedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3190161545663285935L;

	public TabularResourceLockedException() {
		super();
	}

	public TabularResourceLockedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TabularResourceLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TabularResourceLockedException(String message) {
		super(message);
	}

	public TabularResourceLockedException(Throwable cause) {
		super(cause);
	}


}
