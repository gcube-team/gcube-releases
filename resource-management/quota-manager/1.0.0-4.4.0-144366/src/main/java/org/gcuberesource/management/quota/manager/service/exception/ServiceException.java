package org.gcuberesource.management.quota.manager.service.exception;

public class ServiceException extends Exception {

	
	private static final long serialVersionUID = -2255657546267656458L;


	/**
	 * 
	 */
	public ServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
	}
	
	
	public ServiceException(String message,Throwable t) {
		super(message,t);
	}
	

}