package org.gcube.portlets.user.td.monitorwidget.client;




import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MonitorBarUpdater implements MonitorUpdaterListener {
	
	private ProgressBar progressBar;
	
	/**
	 * Creates a new {@link ProgressBar} updater.
	 * @param progressBar the {@link ProgressBar} to update.
	 */
	public MonitorBarUpdater(ProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.updateProgress(0, "Please Wait...");
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void monitorComplete(OperationMonitor operationMonitor) {
		Log.info("Completed");
		progressBar.updateProgress(1, "Completed");	
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void monitorFailed(Throwable caught, String reason, String failureDetails, OperationMonitor operationMonitor) {
		Log.info("Failed");
		progressBar.updateText("Failed");
	}
	
	@Override
	public void monitorInitializing(OperationMonitor operationMonitor) {
		Log.info("Inizializing");
		progressBar.updateProgress(0, "Initializing...");
	}
	
	@Override
	public void monitorUpdate(OperationMonitor operationMonitor) {
		float elaborated=operationMonitor.getTask().getProgress();
		Log.info("Elaborated: "+elaborated);
		if (elaborated>=0 && elaborated<1) {
			Log.trace("progress "+elaborated);
			int elab=new Float(elaborated*100).intValue();
			progressBar.updateProgress(elaborated,elab+"% Progress...");
		}
		if (elaborated == 1) progressBar.updateProgress(1, "Completing...");
		
	}
	
	@Override
	public void monitorValidate(OperationMonitor operationMonitor) {
		float elaborated=operationMonitor.getTask().getProgress();
		Log.info("Validation Elaborated: "+elaborated);
		if (elaborated == 0) progressBar.updateProgress(0, "Start Validation...");
		if (elaborated>0 && elaborated<1) {
			Log.trace("Validation progress "+elaborated);
			int elab=new Float(elaborated*100).intValue();
			progressBar.updateProgress(elaborated,elab+"% Validation Progress...");
		}
		if (elaborated == 1) progressBar.updateProgress(1, "Validation...");
	}

	@Override
	public void monitorStopped(String reason, String details, OperationMonitor operationMonitor) {
		Log.debug("Operation Stopped: ["+operationMonitor.getTrId()+", "+reason+", "+details+"]");
		progressBar.updateText("Validations failed");
		
	}
	
	@Override
	public void monitorGeneratingView(OperationMonitor operationMonitor) {
		Log.info("Generating View...");
		progressBar.updateText("Generating View...");
		
	}


	


	@Override
	public void monitorAborted() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void monitorPutInBackground() {
		// TODO Auto-generated method stub
		
	}

}
