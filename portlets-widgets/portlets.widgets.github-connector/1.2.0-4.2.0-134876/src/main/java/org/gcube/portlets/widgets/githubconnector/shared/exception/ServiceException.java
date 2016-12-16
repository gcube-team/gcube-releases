/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.shared.exception;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
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
