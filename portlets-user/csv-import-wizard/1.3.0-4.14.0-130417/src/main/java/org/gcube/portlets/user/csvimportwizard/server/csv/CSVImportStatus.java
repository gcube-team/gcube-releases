/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum CSVImportStatus {
	
	/**
	 * When the GUI ask for a ticket
	 */
	CREATED,
	
	/**
	 *	When the browser is sending the file 
	 */
	UPLOADING,
	
	/**
	 * When the upload is completed.
	 */
	UPLOAD_COMPLETE,
	
	/**
	 * When the user is setting the import parameters 
	 */
	CONFIGURING,
	
	/**
	 * When the import parameters are under checking. 
	 */
	CHECKING,
	
	/**
	 * When the file is sent to the service. 
	 */
	TRANSMITTING,
	
	/**
	 * When the service is importing the csv lines.
	 */
	IMPORTING,
	
	/**
	 * When the import is completed.
	 */
	COMPLETED,
	
	/**
	 * When the import is failed. 
	 */
	FAILED;

}
