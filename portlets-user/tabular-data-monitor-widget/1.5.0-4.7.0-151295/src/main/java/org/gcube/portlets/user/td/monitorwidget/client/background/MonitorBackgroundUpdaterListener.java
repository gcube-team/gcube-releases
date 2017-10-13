package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface MonitorBackgroundUpdaterListener {
	
	/**
	 * Called when the list of opration is updated
	 * 
	 * @param operationMonitorList Operation monitor list
	 */
	public void operationMonitorListUpdated(ArrayList<BackgroundOperationMonitor> operationMonitorList);
	
	
	/**
	 * Called when operation is failed 
	 * 
	 * @param caught Error
	 */
	public void retrieveOperationMonitorListFailed(Throwable caught);
	
	
	
}
