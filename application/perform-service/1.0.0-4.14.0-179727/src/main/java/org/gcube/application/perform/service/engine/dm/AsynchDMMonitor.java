package org.gcube.application.perform.service.engine.dm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorTask;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynchDMMonitor extends DMMonitor {

	private static final Logger log= LoggerFactory.getLogger(AsynchDMMonitor.class);
	
	


	private int sleep = 2000; // Sleep duration in millisecond
	private int delay = 2000; // Delay on first check in millisecond
	private int period = 2000;// Interval between monitoring requests in millisecond
	private ComputationId computationId;
	private SClient sClient;
	private ArrayList<DMMonitorListener> listeners = new ArrayList<DMMonitorListener>();
	private Timer timer;
	private boolean notEnd;

	
	public AsynchDMMonitor(ComputationId computationId, SClient sClient) {
		super(computationId, sClient);
		this.computationId = computationId;
		this.sClient = sClient;

	}

	@Override
	public void start() {
		throw new RuntimeException("Unecpetcted call to start() method");
	}
	
	public void startAsynch() throws DMException {
		try {
			log.debug("Start Monitoring");
			notEnd = true;
			timer = new Timer(false);
			DMMonitorTask dmMonitorTask = new DMMonitorTask(this,
					computationId, sClient, listeners);
			log.debug("Start: " + new Date());
			timer.schedule(dmMonitorTask, delay, period);

			
			log.debug("Scheduled.");

		} catch (Throwable e) {
			throw new DMException(e);

		}
	}
	
	
	
	
	public void add(DMMonitorListener listener) {
		listeners.add(listener);
	}

	public void addAll(ArrayList<DMMonitorListener> listeners) {
		this.listeners.addAll(listeners);
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
