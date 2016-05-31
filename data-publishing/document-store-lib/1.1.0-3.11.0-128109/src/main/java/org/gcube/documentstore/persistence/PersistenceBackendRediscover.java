/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
class PersistenceBackendRediscover implements Runnable {
	
	private final static Logger logger = LoggerFactory.getLogger(PersistenceBackendRediscover.class);
	
	protected final ScheduledExecutorService scheduler;
	
	protected final String context;
	protected final FallbackPersistenceBackend fallbackPersistenceBackend;
	
	public PersistenceBackendRediscover(String context, 
			FallbackPersistenceBackend fallbackPersistenceBackend, 
			long initialDelay, long delay, TimeUnit timeUnit){
		this.context = context;
		this.fallbackPersistenceBackend = fallbackPersistenceBackend;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.scheduler.scheduleAtFixedRate(this, initialDelay, delay, timeUnit);
	}

	@Override
	public void run() {
		logger.debug("Going to rediscover {}", PersistenceBackend.class.getSimpleName());
		PersistenceBackend rediscovered = PersistenceBackendFactory.
			rediscoverPersistenceBackend(fallbackPersistenceBackend, context);
		if(rediscovered!=fallbackPersistenceBackend){
			logger.debug("Another {} was found : {}. "
					+ "Shutting down {} Thread for context {}", 
					PersistenceBackend.class.getSimpleName(), 
					rediscovered.getClass().getSimpleName(), 
					PersistenceBackendRediscover.class.getSimpleName(),
					context);
			scheduler.shutdown();
		}else{
			logger.debug("{} for contaxt {} is still a {}. We will see if next time we will be more lucky.", 
					PersistenceBackend.class.getSimpleName(),
					context,
					FallbackPersistenceBackend.class.getSimpleName());
		}
	}
}
