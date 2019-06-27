package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class InvalidItemException extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidItemException() {
		super();
	}

	public InvalidItemException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidItemException(String message) {
		super(message);
	}

	public InvalidItemException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getErrorMessage() {
		return "invalid item in request";
	}

	@Override
	public int getStatus() {
		return 500;
	}

}
