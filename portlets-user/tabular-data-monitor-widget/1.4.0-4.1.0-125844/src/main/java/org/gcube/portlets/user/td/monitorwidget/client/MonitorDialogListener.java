package org.gcube.portlets.user.td.monitorwidget.client;


import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface MonitorDialogListener {
	
	
	/**
	 * Called when the operation is complete.
	 * @param operationResult TODO
	 */
	public void operationComplete(OperationResult operationResult);

	/**
	 * Called when the operation is failed.
	 *
	 * @param caught
	 * @param reason
	 * @param details
	 */
	public void operationFailed(Throwable caught, String reason, String details);
	
	

	/**
	 * Called when the operation is stopped
	 * @param operationResult TODO
	 * @param reason
	 * @param details
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
