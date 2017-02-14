package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface MonitorBackgroundUpdaterListener {
	
	/**
	 * Called when the list of opration is updated
	 */
	public void operationMonitorListUpdated(ArrayList<BackgroundOperationMonitor> operationMonitorList);
	
	
	/**
	 * Called when operation is failed 
	 */
	public void retrieveOperationMonitorListFailed(Throwable caught);
	
	
	
}
