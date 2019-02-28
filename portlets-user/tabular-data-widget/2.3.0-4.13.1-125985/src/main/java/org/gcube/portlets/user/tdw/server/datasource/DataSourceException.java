/**
 * 
 */
package org.gcube.portlets.user.tdw.server.datasource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DataSourceException extends Exception {

	private static final long serialVersionUID = 3478740829309767716L;

	/**
	 * @param message
	 * @param cause
	 */
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DataSourceException(String message) {
		super(message);
	}

}
