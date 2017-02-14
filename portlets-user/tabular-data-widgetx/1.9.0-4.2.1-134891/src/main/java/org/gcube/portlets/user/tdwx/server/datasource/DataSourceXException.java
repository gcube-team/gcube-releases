/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.datasource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataSourceXException extends Exception {

	private static final long serialVersionUID = 3478740829309767716L;

	/**
	 * @param message
	 * @param cause
	 */
	public DataSourceXException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DataSourceXException(String message) {
		super(message);
	}

}
