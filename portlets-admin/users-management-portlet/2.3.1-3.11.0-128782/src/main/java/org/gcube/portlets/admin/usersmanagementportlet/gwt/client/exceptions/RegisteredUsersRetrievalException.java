package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RegisteredUsersRetrievalException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2810848160613019897L;
	
	public RegisteredUsersRetrievalException() {};
	
	public RegisteredUsersRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

}
