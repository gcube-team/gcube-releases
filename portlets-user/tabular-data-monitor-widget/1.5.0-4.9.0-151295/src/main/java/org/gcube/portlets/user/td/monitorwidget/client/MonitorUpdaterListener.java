package org.gcube.portlets.user.td.monitorwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;

/**
 * Defines a listener for operation progress.
 *
 * @author Giancarlo Panichi
 * 
 *
 */
public interface MonitorUpdaterListener {

	/**
	 * Called when the operation is starting.
	 * 
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorInitializing(OperationMonitor operationMonitor);

	/**
	 * Called when there is a progress for the operation.
	 * 
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorUpdate(OperationMonitor operationMonitor);

	/**
	 * Called when there is a validate for the operation.
	 * 
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorValidate(OperationMonitor operationMonitor);

	/**
	 * Called when the operation is complete.
	 * 
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorComplete(OperationMonitor operationMonitor);

	/**
	 * Called when the operation is failed.
	 * 
	 * @param caught
	 *            Error
	 * @param reason
	 *            Reason
	 * @param details
	 *            Details
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorFailed(Throwable caught, String reason, String details, OperationMonitor operationMonitor);

	/**
	 * Called when the operation is stopped
	 *
	 * 
	 * @param reason
	 *            Reason
	 * @param details
	 *            Details
	 * @param operationMonitor
	 *            Operation monitor
	 */
	public void monitorStopped(String reason, String details, OperationMonitor operationMonitor);

	/**
	 * Called when the operation is aborted
	 * 
	 */
	public void monitorAborted();

	/**
	 * Called when the operation is put in background
	 */
	public void monitorPutInBackground();

	/**
	 * Called when the operation is generating the view
	 * 
	 * @param operationMonitor Operation monitor
	 */
	public void monitorGeneratingView(OperationMonitor operationMonitor);

}
