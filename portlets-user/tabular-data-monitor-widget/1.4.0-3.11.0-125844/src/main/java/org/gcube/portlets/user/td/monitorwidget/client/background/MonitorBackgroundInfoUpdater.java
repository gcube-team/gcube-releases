package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class MonitorBackgroundInfoUpdater extends Timer {

	private ArrayList<MonitorBackgroundInfoUpdaterListener> listeners = new ArrayList<MonitorBackgroundInfoUpdaterListener>();
	private OperationMonitorSession operationMonitorSession;

	public MonitorBackgroundInfoUpdater(
			OperationMonitorSession operationMonitorSession) {
		this.operationMonitorSession = operationMonitorSession;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Log.debug("requesting operation in background ");
		TDGWTServiceAsync.INSTANCE
				.getBackgroundOperationMonitorForSpecificTask(
						operationMonitorSession,
						new AsyncCallback<OperationMonitor>() {

							public void onFailure(Throwable caught) {
								cancel();

								Log.error("Error retrieving operation monitor",
										caught);

								fireRetrieveOperationMonitorFailed(caught);
							}

							public void onSuccess(OperationMonitor result) {
								Log.debug("retrieved Operation Monitor");

								fireOperationMonitorUpdated(result);

							}

						});

	}

	protected void fireOperationMonitorUpdated(OperationMonitor operationMonitor) {
		for (MonitorBackgroundInfoUpdaterListener listener : listeners)
			listener.backgroundOperationMonitorUpdated(operationMonitor);
	}

	protected void fireRetrieveOperationMonitorFailed(Throwable throwable) {
		for (MonitorBackgroundInfoUpdaterListener listener : listeners)
			listener.retrieveBackgroundOperationMonitorFailed(throwable);
	}

	/**
	 * 
	 * 
	 * @param listener
	 */
	public void addListener(MonitorBackgroundInfoUpdaterListener listener) {
		listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeListener(MonitorBackgroundInfoUpdaterListener listener) {
		listeners.remove(listener);
	}

}
