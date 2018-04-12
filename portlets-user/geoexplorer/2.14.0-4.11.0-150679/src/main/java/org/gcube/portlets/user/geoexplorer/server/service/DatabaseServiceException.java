/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.service;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class DatabaseServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseServiceException(){}
	
	/**
	 * @param message
	 */
	public DatabaseServiceException(String message) {
		super(message);
	}
}
