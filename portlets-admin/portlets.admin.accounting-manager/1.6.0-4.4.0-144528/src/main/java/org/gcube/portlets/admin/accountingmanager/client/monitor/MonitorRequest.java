package org.gcube.portlets.admin.accountingmanager.client.monitor;

import org.gcube.portlets.admin.accountingmanager.client.monitor.MonitorRequestEvent.MonitorRequestEventHandler;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerServiceAsync;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class MonitorRequest {

	private Timer monitor = null;
	private Timer timeOut = null;
	private MonitorRequestEventHandler handler;
	private int timeoutLimit = Constants.CLIENT_MONITOR_TIME_OUT_PERIODMILLIS;

	public MonitorRequest() {
		timeOut = new Timer() {
			public void run() {
				timeOutMonitor();
			}

		};

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
		if (timeOut.isRunning()) {
			timeOut.cancel();
		}

	}

	private void timeOutMonitor() {
		stopMonitor();
		MonitorRequestEvent event = new MonitorRequestEvent(
				MonitorRequestType.TimeOut);
		handler.onMonitor(event);

	}

	private void executeCommand() {
		MonitorRequestEvent event = new MonitorRequestEvent(
				MonitorRequestType.Period);
		handler.onMonitor(event);

	}

	public void addHandler(MonitorRequestEventHandler handler) {
		this.handler = handler;
	}

	public void start() {
		retrieveTimeOut();
	}

	private void startSchedule() {
		// Execute the timer to expire 2 seconds in the future
		monitor.schedule(Constants.CLIENT_MONITOR_PERIODMILLIS);
		timeOut.schedule(timeoutLimit);
	}

	public void repeat() {
		monitor.schedule(Constants.CLIENT_MONITOR_PERIODMILLIS);
	}

	public void stop() {
		stopMonitor();
	}

	private void retrieveTimeOut() {
		AccountingManagerServiceAsync.INSTANCE
				.getClientMonitorTimeout(new AsyncCallback<Long>() {

					@Override
					public void onSuccess(Long timeout) {
						Log.debug("Client Monitor timeout in millis: "
								+ timeout);
						timeoutLimit = timeout.intValue();
						startSchedule();
					}

					@Override
					public void onFailure(Throwable caught) {
						Log.debug("Use default timeoutLimit", caught);
						timeoutLimit = Constants.CLIENT_MONITOR_TIME_OUT_PERIODMILLIS;
						startSchedule();
					}
				});
	}

}
