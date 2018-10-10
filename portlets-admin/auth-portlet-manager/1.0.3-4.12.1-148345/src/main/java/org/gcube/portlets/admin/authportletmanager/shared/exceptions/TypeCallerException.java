package org.gcube.portlets.admin.authportletmanager.shared.exceptions;

/**
 * 
 * @author "Alessandro Pieve" 
 * <a href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a> 
 *
 */
public class TypeCallerException extends Exception {

	


	/**
	 * 
	 */
	private static final long serialVersionUID = -2210435562417868778L;


	/**
	 * 
	 */
	public TypeCallerException() {
		super();
	}

	/**
	 * @param message
	 */
	public TypeCallerException(String message) {
		super(message);
	}
	
	
	public TypeCallerException(String message,Throwable t) {
		super(message,t);
	}
	

}