package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CurrentGroupRetrievalException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950367860688552572L;
	

	public CurrentGroupRetrievalException() {};
	
	public CurrentGroupRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
}
