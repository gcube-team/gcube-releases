/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.progress;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum OperationState {
	
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
