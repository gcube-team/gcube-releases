package org.gcube.portlets.user.td.gwtservice.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class TDGWTIsFinalException  extends TDGWTServiceException {

	
	private static final long serialVersionUID = 4306091799912937920L;
	
	/**
	 * 
	 */
	public TDGWTIsFinalException() {
		super();
	}

	/**
	 * @param message
	 */
	public TDGWTIsFinalException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param t
	 */
	public TDGWTIsFinalException(String message,Throwable t) {
		super(message,t);
	}
	
}
