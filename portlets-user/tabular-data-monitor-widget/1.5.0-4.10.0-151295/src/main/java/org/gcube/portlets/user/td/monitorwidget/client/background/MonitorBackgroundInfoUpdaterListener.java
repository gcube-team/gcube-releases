package org.gcube.portlets.user.td.monitorwidget.client.background;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface MonitorBackgroundInfoUpdaterListener {
	
	/**
	 * Called when the operation is updated
	 * 
	 * @param operationMonitor Operation monitor
	 */
	public void backgroundOperationMonitorUpdated(OperationMonitor operationMonitor);
	
	
	/**
	 * Called when operation is failed 
	 * 
	 * @param caught Error
	 */
	public void retrieveBackgroundOperationMonitorFailed(Throwable caught);
	
	
	
}
