package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class ItemLockedException extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemLockedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ItemLockedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ItemLockedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ItemLockedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getErrorMessage() {
		return "item locked";
	}

	@Override
	public int getStatus() {
		return 409;
	}

}
