/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDGWTServiceException extends Exception {

	private static final long serialVersionUID = -9066034060104406559L;

	/**
	 * 
	 */
	public TDGWTServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public TDGWTServiceException(String message) {
		super(message);
	}
	
	
	public TDGWTServiceException(String message,Throwable t) {
		super(message,t);
	}
	

}
