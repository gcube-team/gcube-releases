/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.shared.exceptions;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ContextException extends Exception {

	
	private static final long serialVersionUID = -2255657546267656458L;


	/**
	 * 
	 */
	public ContextException() {
		super();
	}

	/**
	 * @param message
	 */
	public ContextException(String message) {
		super(message);
	}
	
	
	public ContextException(String message,Throwable t) {
		super(message,t);
	}
	

}