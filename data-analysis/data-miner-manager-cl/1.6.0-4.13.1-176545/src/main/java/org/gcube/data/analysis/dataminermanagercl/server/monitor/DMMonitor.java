package org.gcube.data.analysis.dataminermanagercl.server.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataManager basic Monitor
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DMMonitor {
	private static Logger logger = LoggerFactory.getLogger(DMMonitor.class);
	private int sleep = 2000; // Sleep duration in millisecond
	private int delay = 2000; // Delay on first check in millisecond
	private int period = 2000;// Interval between monitoring requests in millisecond
	private ComputationId computationId;
	private SClient sClient;
	private ArrayList<DMMonitorListener> listeners = new ArrayList<DMMonitorListener>();
	private Timer timer;
	private boolean notEnd;

	public DMMonitor(ComputationId computationId, SClient sClient) {
		logger.debug("DMMonitor");
		this.computationId = computationId;
		this.sClient = sClient;

	}

	public void add(DMMonitorListener listener) {
		listeners.add(listener);
	}

	public void addAll(ArrayList<DMMonitorListener> listeners) {
		this.listeners.addAll(listeners);
	}

	public void start() {
		try {
			logger.debug("Start Monitoring");
			notEnd = true;
			timer = new Timer(false);
			DMMonitorTask dmMonitorTask = new DMMonitorTask(this,
					computationId, sClient, listeners);
			logger.debug("Start: " + new Date());
			timer.schedule(dmMonitorTask, delay, period);

			while (notEnd) {
				Thread.sleep(sleep);
			}
			logger.debug("End run");

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();

		}
	}

	public void cancel() {
		if (timer != null)
			timer.cancel();
		notEnd = false;

	}

	/**
	 * 
	 * @return Sleep duration in millisecond
	 */
	public int getSleep() {
		return sleep;
	}

	/**
	 * 
	 * @param sleep Sleep duration in millisecond
	 */
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	
	/**
	 * 
	 * @return Delay on first check in millisecond
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * 
	 * @param delay Delay on first check in millisecond
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * 
	 * @return Interval between monitoring requests in millisecond
	 */
	public int getPeriod() {
		return period;
	}
	
	/**
	 * 
	 * @param period Interval between monitoring requests in millisecond
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

}
