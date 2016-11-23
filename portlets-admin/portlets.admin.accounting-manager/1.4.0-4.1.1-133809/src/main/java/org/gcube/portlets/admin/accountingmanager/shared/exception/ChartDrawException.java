/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.shared.exception;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ChartDrawException extends Exception {

	
	private static final long serialVersionUID = -8737011216478988776L;


	/**
	 * 
	 */
	public ChartDrawException() {
		super();
	}

	/**
	 * @param message
	 */
	public ChartDrawException(String message) {
		super(message);
	}
	
	
	public ChartDrawException(String message,Throwable t) {
		super(message,t);
	}
	

}
