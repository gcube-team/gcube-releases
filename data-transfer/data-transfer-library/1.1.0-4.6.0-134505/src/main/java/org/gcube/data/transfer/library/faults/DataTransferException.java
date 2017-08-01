package org.gcube.data.transfer.library.faults;

public abstract class DataTransferException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8488351139669552406L;

	public DataTransferException() {
		// TODO Auto-generated constructor stub
	}

	public DataTransferException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DataTransferException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public DataTransferException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DataTransferException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
