package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PendingUsersRetrievalException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2093922858230615858L;
	

	public PendingUsersRetrievalException() {};
	
	public PendingUsersRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PendingUsersRetrievalException(String message) {
		super(message);
	}
}
