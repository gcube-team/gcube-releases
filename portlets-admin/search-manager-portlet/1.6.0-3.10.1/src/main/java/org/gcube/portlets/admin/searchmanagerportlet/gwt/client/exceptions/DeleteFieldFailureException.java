package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DeleteFieldFailureException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -424657990155294238L;
	
	public DeleteFieldFailureException() {}
	
	public DeleteFieldFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
