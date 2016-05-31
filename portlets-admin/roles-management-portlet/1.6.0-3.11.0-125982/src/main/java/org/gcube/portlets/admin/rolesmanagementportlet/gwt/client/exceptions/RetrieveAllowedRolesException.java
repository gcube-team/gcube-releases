package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RetrieveAllowedRolesException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3485238623103495781L;
	
	public RetrieveAllowedRolesException() {};
	
	public RetrieveAllowedRolesException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
