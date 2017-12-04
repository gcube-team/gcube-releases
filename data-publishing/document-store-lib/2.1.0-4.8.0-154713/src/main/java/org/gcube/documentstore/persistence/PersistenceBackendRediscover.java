/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
class PersistenceBackendRediscover implements Runnable {
	
	private final static Logger logger = LoggerFactory.getLogger(PersistenceBackendRediscover.class);
	
	//protected final ScheduledExecutorService scheduler;
	
	protected final String context;
	protected final FallbackPersistenceBackend fallbackPersistenceBackend;
	
	private ScheduledFuture<?> scheduledThread;
	
	public PersistenceBackendRediscover(String context, 
			FallbackPersistenceBackend fallbackPersistenceBackend, 
			long initialDelay, long delay, TimeUnit timeUnit){
		this.context = context;
		this.fallbackPersistenceBackend = fallbackPersistenceBackend;
		scheduledThread = ExecutorUtils.scheduler.scheduleAtFixedRate(this, initialDelay, delay, timeUnit);
	}

	@Override
	public void run() {
		logger.trace("Going to rediscover {}", PersistenceBackend.class.getSimpleName());
		PersistenceBackend rediscovered = PersistenceBackendFactory.
			rediscoverPersistenceBackend(fallbackPersistenceBackend, context);
		if(rediscovered!=fallbackPersistenceBackend){
			logger.trace("Another {} was found : {}. "
					+ "Shutting down {} Thread for context {}", 
					PersistenceBackend.class.getSimpleName(), 
					rediscovered.getClass().getSimpleName(), 
					PersistenceBackendRediscover.class.getSimpleName(),
					context);
			//scheduler.shutdown();
			scheduledThread.cancel(true);
		}else{
			logger.trace("{} for context {} is still a {}. We will see if next time we will be more lucky.", 
					PersistenceBackend.class.getSimpleName(),
					context,
					FallbackPersistenceBackend.class.getSimpleName());
		}
	}
}
