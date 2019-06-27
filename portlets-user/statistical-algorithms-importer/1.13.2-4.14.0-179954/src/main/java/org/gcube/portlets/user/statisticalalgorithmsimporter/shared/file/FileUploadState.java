/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public enum FileUploadState {
	
	/**
	 * The operation is started
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
