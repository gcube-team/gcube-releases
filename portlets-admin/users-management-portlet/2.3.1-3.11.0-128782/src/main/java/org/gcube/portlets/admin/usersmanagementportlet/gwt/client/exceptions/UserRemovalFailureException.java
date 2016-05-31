package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserRemovalFailureException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2494455813336241174L;
	
	public UserRemovalFailureException() {};
	
	public UserRemovalFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
