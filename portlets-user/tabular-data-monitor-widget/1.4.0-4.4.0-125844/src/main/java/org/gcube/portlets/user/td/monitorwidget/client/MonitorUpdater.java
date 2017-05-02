package org.gcube.portlets.user.td.monitorwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
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
public class MonitorUpdater extends Timer implements MonitorDialogEventUIListener {
	
	private ArrayList<MonitorUpdaterListener> listeners = new ArrayList<MonitorUpdaterListener>();
	private OperationMonitorSession operationMonitorSession;
	
	public MonitorUpdater(OperationMonitorSession operationMonitorSession){
		this.operationMonitorSession=operationMonitorSession;
		
	}
	 
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Log.debug("requesting operation progress");
		TDGWTServiceAsync.INSTANCE
				.getOperationMonitor(operationMonitorSession,new AsyncCallback<OperationMonitor>() {

					
					public void onFailure(Throwable caught) {
						cancel();
					        
						Log.error("Error retrieving the operation state",
								caught);
						String message = getStack(caught);
						fireMonitorFailed(caught,
								"Failed getting operation updates", message, null);
					}

					public void onSuccess(OperationMonitor result) {
						Log.debug("retrieved OperationMonitor: "
								+ result);
						
						if(result==null){
							return;
						}
						if(result.isInBackground()){
							Log.debug("Operation is in background");
							cancel();
							fireMonitorPutInBackground();
							return;
						}
						
						if(result.isAbort()){
							Log.debug("Operation is aborted");
							cancel();
							fireMonitorAborted();
							
						}
						
						if(result.getTask()==null|| result.getTask().getState()==null){
							return;
						}
						
						switch (result.getTask().getState()) {
						case INITIALIZING:
							Log.info("Initializing...");
							fireMonitorInitializing(result);
							break;
						case ABORTED:
							cancel();
							fireMonitorAborted();
							Log.info("Aborted");
							break;
						case IN_PROGRESS:
							fireMonitorUpdate(result);
						
							break;
						case VALIDATING_RULES:
							fireMonitorValidate(result);
							break;	
						case GENERATING_VIEW:
							Log.info("Generating View...");
							fireMonitorGeneratingView(result);
							break;		
						case STOPPED:
							cancel();
							stopMessage(result);
							break;		
						case FAILED:
							cancel();
							errorMessage(result);
							break;
						case SUCCEDED:
							cancel();
							Log.info("Fisnish :"
									+ result.getTrId());
							fireMonitorComplete(result);
							break;
						default:
							Log.info("Unknow State");
							break;
						}

					}

					

				});

	}

	
	protected void errorMessage(OperationMonitor result) {
		Log.info("Operation Failed");
		Throwable th;
		String reason = null;
		String details = null;
		if (result.getTask().getErrorCause() != null) {
			th = result.getTask().getErrorCause();
			reason = "Failed Service";
			details = result.getTask().getErrorCause().getLocalizedMessage();
		} else {
			th = new Throwable("Operation failed");
			reason = "Error on Service";
			details = "Operation failed";
		}
		
		fireMonitorFailed(th, reason, details,result);
	}
	
	protected void stopMessage(OperationMonitor result) {
		Log.info("Operation Stopped");
		String reason = null;
		String details = null;
		if (result.getTask().getErrorCause() != null) {	
			reason = "Validations failed";
			details = result.getTask().getErrorCause().getLocalizedMessage();
		} else {
			reason = "Validations failed";
			details = "Operation stopped";
		}
	
		fireMonitorStopped(reason, details,result);
	}
	

	protected String getStack(Throwable e) {
		String message = e.getLocalizedMessage() + " -> <br>";
		Throwable c = e.getCause();
		if (c != null)
			message += getStack(c);
		return message;
	}

	protected void fireMonitorInitializing(OperationMonitor result) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorInitializing(result);
	}
	
	protected void fireMonitorGeneratingView(OperationMonitor result) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorGeneratingView(result);
	}

	protected void fireMonitorUpdate(OperationMonitor result) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorUpdate(result);
	}
	
	protected void fireMonitorValidate(OperationMonitor result) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorValidate(result);
	}

	protected void fireMonitorComplete(OperationMonitor result) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorComplete(result);
	}

	protected void fireMonitorFailed(Throwable caught, String failure,
			String failureDetails, OperationMonitor operationMonitor) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorFailed(caught, failure, failureDetails, operationMonitor);
	}

	protected void fireMonitorStopped(String reason, String details, OperationMonitor operationMonitor) {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorStopped(reason, details, operationMonitor);
	}
	
	protected void fireMonitorAborted() {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorAborted();
	}
	
	protected void fireMonitorPutInBackground() {
		for (MonitorUpdaterListener listener : listeners)
			listener.monitorPutInBackground();
	}
	
	
	/**
	 * Add a new {@link MonitorUpdaterListener} to this
	 * {@link AddColumnProgressUpdater}.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addListener(MonitorUpdaterListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the specified {@link MonitorUpdaterListener} from this
	 * {@link AddColumnProgressUpdater}.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(MonitorUpdaterListener listener) {
		listeners.remove(listener);
	}




	@Override
	public void requestAborted() {
		operationMonitorSession.setAbort(true);
		
	}


	@Override
	public void requestPutInBackground() {
		operationMonitorSession.setInBackground(true);
		
	}
}
