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
public class AccountingManagerServiceException extends Exception {

	
	private static final long serialVersionUID = -2255657546267656458L;


	/**
	 * 
	 */
	public AccountingManagerServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public AccountingManagerServiceException(String message) {
		super(message);
	}
	
	
	public AccountingManagerServiceException(String message,Throwable t) {
		super(message,t);
	}
	

}
