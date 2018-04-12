package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;
/**
 * 
 */



public enum ImportStatus {
	
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
	 * When the file is sent to the service. 
	 */
	TRANSMITTING,
	
	/**
	 * When the service is importing the  lines.
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
