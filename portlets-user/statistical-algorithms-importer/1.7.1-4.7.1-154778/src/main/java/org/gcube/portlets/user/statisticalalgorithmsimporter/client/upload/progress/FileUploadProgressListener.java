/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload.progress;

/**
 * Defines a listener for file upload progress.
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
	 * 
	 * @param elaborated
	 *            the elaborated part.
	 */
	public void operationUpdate(float elaborated);

	/**
	 * Called when the operation is complete.
	 */
	public void operationComplete();

	/**
	 * Called when the operation is failed.
	 * 
	 * 
	 * @param caught
	 *            error
	 * @param reason
	 *            reason
	 * @param failureDetails
	 *            details
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails);
}
