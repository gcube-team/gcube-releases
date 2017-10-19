package org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.monitor.MonitorDeployOperationEvent.MonitorDeployOperationEventHandler;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;

import com.google.gwt.user.client.Timer;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class MonitorDeployOperation {

	private Timer monitor = null;
	
	private MonitorDeployOperationEventHandler handler;
	
	public MonitorDeployOperation() {
		

		monitor = new Timer() {
			public void run() {
				executeCommand();
			}
		};

	}

	private void stopMonitor() {
		if (monitor.isRunning()) {
			monitor.cancel();
		}
		

	}


	private void executeCommand() {
		MonitorDeployOperationEvent event = new MonitorDeployOperationEvent();
		handler.onMonitor(event);

	}

	public void addHandler(MonitorDeployOperationEventHandler handler) {
		this.handler = handler;
	}

	public void start() {
		startSchedule();
	}

	private void startSchedule() {
		// Execute the timer to expire 2 seconds in the future
		monitor.schedule(Constants.CLIENT_MONITOR_PERIODMILLIS);
		
	}
	
	public void repeat() {
		monitor.schedule(Constants.CLIENT_MONITOR_PERIODMILLIS);
	}
	
	public void stop() {
		stopMonitor();
	}

	

}