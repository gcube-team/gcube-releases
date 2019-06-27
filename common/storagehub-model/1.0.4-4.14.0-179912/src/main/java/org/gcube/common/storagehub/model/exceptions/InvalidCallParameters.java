package org.gcube.common.storagehub.model.exceptions;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
public class InvalidCallParameters extends StorageHubException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCallParameters() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidCallParameters(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidCallParameters(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidCallParameters(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getErrorMessage() {
		return "invalid call parameter";
	}

	@Override
	public int getStatus() {
		return 400;
	}
	
}
