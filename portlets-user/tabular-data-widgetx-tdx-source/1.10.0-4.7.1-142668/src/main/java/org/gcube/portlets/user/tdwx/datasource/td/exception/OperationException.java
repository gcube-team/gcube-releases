package org.gcube.portlets.user.tdwx.datasource.td.exception;

import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OperationException extends DataSourceXException {

	private static final long serialVersionUID = -8593898774262390757L;

	/**
	 * 
	 * @param message
	 */
	public OperationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OperationException(String message, Throwable cause) {
		super(message, cause);
	}

}
