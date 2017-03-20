/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.file;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
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
