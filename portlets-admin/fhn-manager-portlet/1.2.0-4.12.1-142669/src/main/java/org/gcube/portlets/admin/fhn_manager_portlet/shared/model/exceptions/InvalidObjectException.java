package org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InvalidObjectException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidObjectException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidObjectException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
	
}
