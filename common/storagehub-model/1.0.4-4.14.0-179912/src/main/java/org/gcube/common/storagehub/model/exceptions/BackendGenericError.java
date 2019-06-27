package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class BackendGenericError extends StorageHubException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BackendGenericError() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BackendGenericError(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BackendGenericError(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BackendGenericError(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getErrorMessage() {
		return "generic error in the backend";
	}

	@Override
	public int getStatus() {
		return 500;
	}
	
}
