package org.gcube.application.framework.core.session;

import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Notifier {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Notifier.class);

	Semaphore sem;
	
	Notifier() {
		 sem = new Semaphore(0, true);
	}

	
	public void waitNotification() throws InterruptedException
	{
		sem.acquire();
	}
	
	public void notifyAllWaiting() throws InterruptedException
	{
		logger.debug("Sending wake up signal to " + sem.getQueueLength() + " receivers...");
		sem.release(sem.getQueueLength()); 
	}
}
