/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.progress;

/**
 * Defines a listener for operation progress.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface OperationProgressListener {
	
	/**
	 * Called when there is a progress for the operation.
	 * @param elaborated the elaborated part.
	 */
	public void operationUpdate(long total, long elaborated);
	
	/**
	 * Called when the operation is complete.
	 */
	public void operationComplete();

	/**
	 * Called when the operation is failed.
	 * @param caught the failure exception.
	 * @param reason the failure reason.
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails);
}
