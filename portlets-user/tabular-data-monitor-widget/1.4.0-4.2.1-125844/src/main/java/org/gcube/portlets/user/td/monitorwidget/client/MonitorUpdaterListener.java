package org.gcube.portlets.user.td.monitorwidget.client;


import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;



/**
 * Defines a listener for operation progress.
 *
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface MonitorUpdaterListener {
	
	/**
	 * Called when the operation is starting.
	 */
	public void monitorInitializing(OperationMonitor operationMonitor);
	
	/**
	 * Called when there is a progress for the operation.
	 * @param elaborated the elaborated part.
	 */
	public void monitorUpdate(OperationMonitor operationMonitor);
	
	/**
	 * Called when there is a validate for the operation.
	 * @param elaborated the elaborated part.
	 */
	public void monitorValidate(OperationMonitor operationMonitor);	
	
	/**
	 * Called when the operation is complete.
	 * @param operationMonitor 
	 */
	public void monitorComplete(OperationMonitor operationMonitor);

	/**
	 * Called when the operation is failed.
	 * 
	 * @param caught
	 * @param reason
	 * @param details
	 * @param operationMonitor 
	 */
	public void monitorFailed(Throwable caught, String reason, String details, OperationMonitor operationMonitor);
	
	/**
	 * Called when the operation is stopped
	 *
	 * @param reason
	 * @param details
	 * @param operationMonitor 
	 */
	public void monitorStopped(String reason, String details, OperationMonitor operationMonitor);
	
	
	/**
	 * Called when the operation is aborted
	 */
	public void monitorAborted();
	
	
	/**
	 * Called when the operation is put in background 
	 */
	public void monitorPutInBackground();
	
	
	/**
	 *  Called when the operation is generating the view
	 */
	public void monitorGeneratingView(OperationMonitor operationMonitor);
	
	
	
	
}
