package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MonitorBackgroundUpdater extends Timer implements MonitorBackgroundEventUIListener {
	
	private ArrayList<MonitorBackgroundUpdaterListener> listeners = new ArrayList<MonitorBackgroundUpdaterListener>();
	private BackgroundOperationMonitorSession backgroundOperationMonitorSession;
	
	public MonitorBackgroundUpdater(){
		backgroundOperationMonitorSession=new BackgroundOperationMonitorSession();
	}
	 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Log.debug("requesting list of operation in background ");
		TDGWTServiceAsync.INSTANCE
				.getBackgroundOperationMonitor(backgroundOperationMonitorSession, new AsyncCallback<ArrayList<BackgroundOperationMonitor>>() {

					
					public void onFailure(Throwable caught) {
						cancel();
					        
						Log.error("Error retrieving operation monitor list",
								caught);
						
						fireRetrieveOperationMonitorListFailed(caught);
					}

					public void onSuccess(ArrayList<BackgroundOperationMonitor> result) {
						Log.debug("retrieved Operation Monitor List: "
								+ result.size());
						
						fireOperationMonitorListUpdated(result);
						
					}

					

				});

	}

	
	protected void fireOperationMonitorListUpdated(ArrayList<BackgroundOperationMonitor> operationMonitorList){
		for (MonitorBackgroundUpdaterListener listener : listeners)
			listener.operationMonitorListUpdated(operationMonitorList);
	}
	
	protected void fireRetrieveOperationMonitorListFailed(Throwable throwable){
		for (MonitorBackgroundUpdaterListener listener : listeners)
			listener.retrieveOperationMonitorListFailed(throwable);
	}
	
	
	
	/**
	 * 
	 * 
	 * @param listener
	 */
	public void addListener(MonitorBackgroundUpdaterListener listener) {
		listeners.add(listener);
	}

	
	/**
	 * 
	 * @param listener
	 */
	public void removeListener(MonitorBackgroundUpdaterListener listener) {
		listeners.remove(listener);
	}


	@Override
	public void requestAborted(String taskId) {
		OperationMonitorSession operationMonitorSession=new OperationMonitorSession(taskId);
		operationMonitorSession.setAbort(true);
		backgroundOperationMonitorSession.addToOperationMonitorSessionList(operationMonitorSession);
	}

	@Override
	public void requestHidden(String taskId) {
		OperationMonitorSession operationMonitorSession=new OperationMonitorSession(taskId);
		operationMonitorSession.setHidden(true);
		backgroundOperationMonitorSession.addToOperationMonitorSessionList(operationMonitorSession);
	}


	
	
	@Override
	public void requestResume(String taskId) {
		// TODO Auto-generated method stub
		
	}




	
}
