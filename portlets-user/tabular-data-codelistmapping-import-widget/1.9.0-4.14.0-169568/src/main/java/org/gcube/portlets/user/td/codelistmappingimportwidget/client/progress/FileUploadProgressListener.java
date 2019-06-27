/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;




/**
 * Defines a listener for operation progress.
 *
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface FileUploadProgressListener {
	
	/**
	 * Called when the operation is starting.
	 */
	public void operationInitializing();
	
	/**
	 * Called when there is a progress for the operation.
	 * @param elaborated the elaborated part.
	 */
	public void operationUpdate(float elaborated);
	
	
	/**
	 * Called when the operation is complete.
	 */
	public void operationComplete();

	/**
	 * Called when the operation is failed.
	 * 
	 * @param caught Error
	 * @param reason Reason
	 * @param failureDetails Error details
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails);
}
