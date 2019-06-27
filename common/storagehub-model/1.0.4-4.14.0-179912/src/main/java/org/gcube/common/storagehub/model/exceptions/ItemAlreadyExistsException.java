package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class ItemAlreadyExistsException extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemAlreadyExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ItemAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ItemAlreadyExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ItemAlreadyExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getErrorMessage() {
		return "item already exists";
	}

	@Override
	public int getStatus() {
		return 400;
	}
}
