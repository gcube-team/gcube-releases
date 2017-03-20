package org.gcube.portlets.user.td.monitorwidget.client.background;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface MonitorBackgroundInfoUpdaterListener {
	
	/**
	 * Called when the operation is updated
	 */
	public void backgroundOperationMonitorUpdated(OperationMonitor operationMonitor);
	
	
	/**
	 * Called when operation is failed 
	 */
	public void retrieveBackgroundOperationMonitorFailed(Throwable caught);
	
	
	
}
