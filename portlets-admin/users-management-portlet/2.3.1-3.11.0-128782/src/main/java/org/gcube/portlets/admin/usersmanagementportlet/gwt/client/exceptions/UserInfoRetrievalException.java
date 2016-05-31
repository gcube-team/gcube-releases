package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInfoRetrievalException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 231133649987619413L;
	
	public UserInfoRetrievalException() {};

	public UserInfoRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
}
