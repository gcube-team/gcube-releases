package org.gcube.portlets.admin.authportletmanager.shared.exceptions;

public class ExpiredSessionServiceException  extends  ServiceException{

	
	private static final long serialVersionUID = -2255657546267656458L;


	/**
	 * 
	 */
	public ExpiredSessionServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public ExpiredSessionServiceException(String message) {
		super(message);
	}
	
	
	public ExpiredSessionServiceException(String message,Throwable t) {
		super(message,t);
	}
	

}