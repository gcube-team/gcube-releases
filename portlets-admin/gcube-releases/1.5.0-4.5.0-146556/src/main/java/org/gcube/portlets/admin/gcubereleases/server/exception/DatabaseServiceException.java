package org.gcube.portlets.admin.gcubereleases.server.exception;

/**
 * The Class DatabaseServiceException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class DatabaseServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new database service exception.
	 */
	public DatabaseServiceException(){}
	
	/**
	 * Instantiates a new database service exception.
	 *
	 * @param message the message
	 */
	public DatabaseServiceException(String message) {
		super(message);
	}
}
