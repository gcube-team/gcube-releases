/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.rpc;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CSVImportServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415709826152997218L;

	/**
	 * 
	 */
	protected CSVImportServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public CSVImportServiceException(String message) {
		super(message);
	}

}
