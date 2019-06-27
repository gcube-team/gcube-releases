package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class IdNotFoundException extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6375517955317819916L;

	public IdNotFoundException(String id, Throwable cause) {
		super(String.format("Item with id %s not found", id), cause);
		// TODO Auto-generated constructor stub
	}

	public IdNotFoundException(String id) {
		super(String.format("Item with id %s not found", id));
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getErrorMessage() {
		return "item not found";
	}

	@Override
	public int getStatus() {
		return 404;
	}
	
}
