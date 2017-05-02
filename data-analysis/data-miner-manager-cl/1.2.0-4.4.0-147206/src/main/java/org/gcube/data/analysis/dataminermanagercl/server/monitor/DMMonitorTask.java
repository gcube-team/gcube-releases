package org.gcube.data.analysis.dataminermanagercl.server.monitor;

import java.util.ArrayList;
import java.util.TimerTask;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DMMonitorTask extends TimerTask {
	private static Logger logger = LoggerFactory.getLogger(DMMonitorTask.class);
	private ArrayList<DMMonitorListener> listeners;
	private ComputationId computationId;
	private SClient sClient;
	private DMMonitor dmMonitor;

	public DMMonitorTask(DMMonitor dmMonitor, ComputationId computationId, SClient sClient,
			ArrayList<DMMonitorListener> listeners) {
		this.computationId = computationId;
		this.sClient = sClient;
		this.listeners = listeners;
		this.dmMonitor=dmMonitor;
		logger.debug("DMMonitorTask");

	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		logger.debug("Requesting operation progress");
		ComputationStatus computationStatus = null;
		try {
			computationStatus = sClient.getComputationStatus(computationId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();

		}
		logger.debug("ComputationStatus: " + computationStatus);
		if (computationStatus == null) {
			logger.error("ComputationStatus is null");
			return;
		}

		Status status = computationStatus.getStatus();
		if (status == null) {
			logger.error("Status is null");
			return;
		}

		switch (status) {
		case ACCEPTED:
			fireAccepted(computationStatus);
			break;
		case CANCELLED:
			dmMonitor.cancel();
			fireCancelled(computationStatus);
			break;
		case COMPLETE:
			dmMonitor.cancel();
			fireComplete(computationStatus);
			break;
		case FAILED:
			dmMonitor.cancel();
			fireFailed(computationStatus);
			break;
		case RUNNING:
			fireRunning(computationStatus);
			break;
		default:
			break;

		}

	}

	private void fireAccepted(ComputationStatus computationStatus) {
		for (DMMonitorListener listener : listeners)
			listener.accepted();
	}

	private void fireCancelled(ComputationStatus computationStatus) {
		for (DMMonitorListener listener : listeners)
			listener.cancelled();
	}

	private void fireComplete(ComputationStatus computationStatus) {
		for (DMMonitorListener listener : listeners)
			listener.complete(computationStatus.getPercentage());
	}

	private void fireFailed(ComputationStatus computationStatus) {
		for (DMMonitorListener listener : listeners)
			listener.failed(computationStatus.getMessage(),
					computationStatus.getError());
	}

	private void fireRunning(ComputationStatus computationStatus) {
		for (DMMonitorListener listener : listeners)
			listener.running(computationStatus.getPercentage());
	}

}
