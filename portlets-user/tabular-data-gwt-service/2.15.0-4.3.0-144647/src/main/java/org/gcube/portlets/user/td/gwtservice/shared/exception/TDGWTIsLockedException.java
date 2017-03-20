package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class TDGWTIsLockedException  extends TDGWTServiceException {

	
	private static final long serialVersionUID = 4306091799912937920L;
	
	/**
	 * 
	 */
	public TDGWTIsLockedException() {
		super();
	}

	/**
	 * @param message
	 */
	public TDGWTIsLockedException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param t
	 */
	public TDGWTIsLockedException(String message,Throwable t) {
		super(message,t);
	}
	
}
