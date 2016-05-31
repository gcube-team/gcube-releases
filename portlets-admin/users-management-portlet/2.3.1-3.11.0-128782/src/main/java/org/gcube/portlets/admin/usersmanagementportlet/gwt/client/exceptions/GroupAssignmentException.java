package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupAssignmentException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4970079843506321621L;
	
	public GroupAssignmentException() {};
	
	public GroupAssignmentException(String message, Throwable cause) {
		super(message, cause);
	}

}
