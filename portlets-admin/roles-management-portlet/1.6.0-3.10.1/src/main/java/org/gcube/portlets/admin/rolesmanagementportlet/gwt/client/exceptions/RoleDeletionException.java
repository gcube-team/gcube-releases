package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RoleDeletionException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6878586529985030655L;
	
	public RoleDeletionException() {};
	
	public RoleDeletionException(String message, Throwable cause) {
		super(message, cause);
	}

}
