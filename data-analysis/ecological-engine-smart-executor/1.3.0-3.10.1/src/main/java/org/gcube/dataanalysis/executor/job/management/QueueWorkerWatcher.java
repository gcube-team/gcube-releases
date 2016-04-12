package org.gcube.dataanalysis.executor.job.management;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.Message;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.executor.messagequeue.ATTRIBUTE;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QCONSTANTS;

public class QueueWorkerWatcher {

	protected int maxwaitingTime = 2*QueueJobManager.queueWatcherMaxwaitingTime;
	private long lastTimeClock;
	Timer watcher;
	Producer producer;
	Map<String, Object> message;
	public boolean resent=false;
	int order;
	
	public QueueWorkerWatcher(Producer producer, Map<String, Object> message, int order) {
		this.producer = producer;
		this.message = message;
		resent=false;
		this.order = order;
		
		watcher = new Timer();
		watcher.schedule(new Controller(), 0, QCONSTANTS.refreshStatusTime);
		resetTime();
	}

	public synchronized void resetTime() {
		lastTimeClock = System.currentTimeMillis();
	}

	public synchronized void destroy() {
		if (watcher != null) {
			watcher.cancel();
			watcher.purge();
			watcher = null;
		}
	}

	public boolean hasResent(){
		return resent;
	}
	
	private class Controller extends TimerTask {

		@Override
		public void run() {
			try {
				long t0 = System.currentTimeMillis();
				AnalysisLogger.getLogger().debug("Watcher "+order+" Timing Is "+(t0 - lastTimeClock)+ " max waiting time: "+maxwaitingTime);
				if ((t0 - lastTimeClock) > maxwaitingTime) {
					
					AnalysisLogger.getLogger().info("Watcher "+order+" Time Is Over "+(t0 - lastTimeClock));
					
					AnalysisLogger.getLogger().info("Watcher "+order+" Re-Sending Message "+message);
					producer.sendMessage(message, QCONSTANTS.timeToLive);
//					QueueJobManager.resentMessages[Integer.parseInt(""+message.get(ATTRIBUTE.ORDER.name()))]=QueueJobManager.resentMessages[Integer.parseInt(""+message.get(ATTRIBUTE.ORDER.name()))]+1;
					resent = true;
					AnalysisLogger.getLogger().info("Watcher "+order+" Destroying watcher");
					destroy();
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
