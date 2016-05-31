package org.gcube.portlets.admin.accountingmanager.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class AccountingManagerSessionExpiredException  extends AccountingManagerServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public AccountingManagerSessionExpiredException() {
		super();
	}

	/**
	 * @param message
	 */
	public AccountingManagerSessionExpiredException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param t
	 */
	public AccountingManagerSessionExpiredException(String message,Throwable t) {
		super(message,t);
	}
	
}
