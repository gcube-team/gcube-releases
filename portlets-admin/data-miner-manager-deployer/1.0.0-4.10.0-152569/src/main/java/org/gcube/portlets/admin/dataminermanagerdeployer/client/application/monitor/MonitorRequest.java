package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor;

import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor.MonitorRequestEvent.MonitorRequestEventHandler;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.Constants;

import com.google.gwt.user.client.Timer;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class MonitorRequest {
	
	private Timer monitor = null;
	
	private MonitorRequestEventHandler handler;
	
	public MonitorRequest() {
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
		MonitorRequestEvent event = new MonitorRequestEvent();
		handler.onMonitor(event);

	}

	public void addHandler(MonitorRequestEventHandler handler) {
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