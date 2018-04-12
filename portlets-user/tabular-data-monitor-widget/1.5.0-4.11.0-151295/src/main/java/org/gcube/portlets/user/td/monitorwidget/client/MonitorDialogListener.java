package org.gcube.portlets.user.td.monitorwidget.client;


import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface MonitorDialogListener {
	
	
	/**
	 * Called when the operation is complete.
	 * @param operationResult Operation result
	 */
	public void operationComplete(OperationResult operationResult);

	/**
	 * Called when the operation is failed.
	 *
	 * @param caught Error
	 * @param reason Reason
	 * @param details Details
	 */
	public void operationFailed(Throwable caught, String reason, String details);
	
	

	/**
	 * Called when the operation is stopped
	 * 
	 * @param operationResult Operation result
	 * @param reason Reason
	 * @param details Details
	 */
	public void operationStopped(OperationResult operationResult, String reason, String details);
	
	
	/**
	 * Called when the operation is aborted
	 * 
	 */
	public void operationAborted();
	
	
	/**
	 * Called when the operation is put in the background
	 * 
	 */
	public void operationPutInBackground();
	
}
