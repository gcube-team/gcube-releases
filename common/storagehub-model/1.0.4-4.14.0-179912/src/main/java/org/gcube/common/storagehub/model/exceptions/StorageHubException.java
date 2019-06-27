package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public abstract class StorageHubException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StorageHubException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public abstract String getErrorMessage();
	
	public abstract int getStatus();
	
	public StorageHubException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public StorageHubException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StorageHubException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
	
}
