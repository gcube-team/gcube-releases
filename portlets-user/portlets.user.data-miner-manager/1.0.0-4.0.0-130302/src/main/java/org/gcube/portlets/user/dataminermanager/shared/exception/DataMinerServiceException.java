package org.gcube.portlets.user.dataminermanager.shared.exception;

/**
 * DataMiner Service Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class DataMinerServiceException extends
		ServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public DataMinerServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public DataMinerServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param t
	 */
	public DataMinerServiceException(String message, Throwable t) {
		super(message, t);
	}

}
