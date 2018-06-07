/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.file;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public enum FileUploadState {
	
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
