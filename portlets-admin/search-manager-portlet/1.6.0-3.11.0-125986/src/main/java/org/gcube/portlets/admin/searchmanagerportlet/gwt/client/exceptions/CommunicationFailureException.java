package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommunicationFailureException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -424657990155294238L;
	
	public CommunicationFailureException() {}
	
	public CommunicationFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
