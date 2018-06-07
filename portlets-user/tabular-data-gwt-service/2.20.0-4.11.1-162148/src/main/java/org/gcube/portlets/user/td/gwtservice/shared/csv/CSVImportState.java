/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public enum CSVImportState {
	
	/**
	 * The operation is in progress.
	 */
	STARTED,
	/**
	 * The operation is in progress.
	 */
	INPROGRESS,
	
	/**
	 * The operation is completed.
	 */
	COMPLETED,
	
	/**
	 * The operation is failed. 
	 */
	FAILED;
}
