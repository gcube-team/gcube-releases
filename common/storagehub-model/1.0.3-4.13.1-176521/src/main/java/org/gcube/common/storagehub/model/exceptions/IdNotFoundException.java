package org.gcube.common.storagehub.model.exceptions;

public class IdNotFoundException extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdNotFoundException(String id, Throwable cause) {
		super(String.format("Item with id %s not found", id), cause);
		// TODO Auto-generated constructor stub
	}

	public IdNotFoundException(String id) {
		super(String.format("Item with id %s not found", id));
		// TODO Auto-generated constructor stub
	}

	
	
}
