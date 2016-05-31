package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RolesRetrievalException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6854575547365235529L;
	
	public RolesRetrievalException() { };
	
	public RolesRetrievalException(String message) {
		super(message);
	}

	public RolesRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
}
