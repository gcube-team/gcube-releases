/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.progress;




/**
 * Defines a listener for operation progress.
 *
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
	 * @param caught the failure exception.
	 * @param reason the failure reason.
	 */
	public void operationFailed(Throwable caught, String reason, String failureDetails);
}
