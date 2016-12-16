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
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DMMonitor {
	private static Logger logger = LoggerFactory.getLogger(DMMonitor.class);
	private static final int SLEEP = 1000;
	private static final int DELAY = 2000;
	private static final int PERIOD = 1000;
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
			notEnd=true;
			timer = new Timer(false);
			DMMonitorTask dmMonitorTask = new DMMonitorTask(this, computationId,
					sClient, listeners);
			logger.debug("Start: " + new Date());
			timer.schedule(dmMonitorTask, DELAY, PERIOD);
	
			while (notEnd){
				Thread.sleep(SLEEP);
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
		notEnd=false;
		
	}

	

}
