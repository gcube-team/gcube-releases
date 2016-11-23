package org.gcube.portlets.widgets.githubconnector.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ExpiredSessionServiceException extends
		ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public ExpiredSessionServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public ExpiredSessionServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param t
	 */
	public ExpiredSessionServiceException(String message, Throwable t) {
		super(message, t);
	}

}
