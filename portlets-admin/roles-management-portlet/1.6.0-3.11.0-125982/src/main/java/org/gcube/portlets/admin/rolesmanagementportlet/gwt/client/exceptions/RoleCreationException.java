package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RoleCreationException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6419934386726984494L;
	
	public RoleCreationException() {};
	
	public RoleCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
