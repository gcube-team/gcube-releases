/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.records.aggregation.AggregationScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class PersistenceBackendFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceBackendFactory.class);
	
	public static final String DEFAULT_CONTEXT = "DEFAULT_CONTEXT";
	
	public final static String HOME_SYSTEM_PROPERTY = "user.home";
	
	protected static final String FALLBACK_FILENAME = "fallback.log";
	
	private static String fallbackLocation;

	private static Map<String, PersistenceBackend> persistenceBackends;
	private static Map<String, Long> fallbackLastCheck;
	
	public static final long FALLBACK_RETRY_TIME = 1000*60*10; // 10 min
	
	/**
	 * @return the fallbackLastCheck
	 */
	protected static Long getFallbackLastCheck(String scope) {
		return fallbackLastCheck.get(scope);
	}
	
	static {
		persistenceBackends = new HashMap<String, PersistenceBackend>();
		fallbackLastCheck = new HashMap<String, Long>();
	}
	
	private static File file(File file) throws IllegalArgumentException {
		if(!file.isDirectory()){
			file = file.getParentFile();
		}
		// Create folder structure if not exist
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	public synchronized static void setFallbackLocation(String path){
		if(fallbackLocation == null){
			if(path==null){
				path = System.getProperty(HOME_SYSTEM_PROPERTY);
			}
			file(new File(path));
			fallbackLocation = path;
		}
	}
	
	protected synchronized static String getFallbackLocation(){
		if(fallbackLocation==null){
			try {
				return System.getProperty(HOME_SYSTEM_PROPERTY);
			}catch(Exception e){
				return ".";
			}
		}
		return fallbackLocation;
	}
	
	protected static String sanitizeContext(final String context){
		if(context==null || context.compareTo("")==0){
			return DEFAULT_CONTEXT;
		}
		return context;
	}
	
	protected static String removeSlashFromContext(String context){
		return context.replace("/", "_");
	}
	
	public static File getFallbackFile(String context){
		context = sanitizeContext(context);
		String slashLessContext = removeSlashFromContext(context);
		File fallbackFile = new File(getFallbackLocation(), String.format("%s.%s", slashLessContext, FALLBACK_FILENAME));
		return fallbackFile;
	}
	
	protected static FallbackPersistenceBackend createFallback(String context){
		context = sanitizeContext(context);
		logger.debug("Creating {} for context {}", FallbackPersistenceBackend.class.getSimpleName(), context);
		File fallbackFile = getFallbackFile(context);
		logger.trace("{} for context {} is {}", FallbackPersistenceBackend.class.getSimpleName(), context, fallbackFile.getAbsolutePath());
		FallbackPersistenceBackend fallbackPersistence = new FallbackPersistenceBackend(fallbackFile);
		fallbackPersistence.setAggregationScheduler(AggregationScheduler.newInstance(new DefaultPersitenceExecutor(fallbackPersistence)));
		return fallbackPersistence;
	}
	
	protected static PersistenceBackend discoverPersistenceBackend(String context){
		context = sanitizeContext(context);
		logger.debug("Discovering {} for scope {}", 
				PersistenceBackend.class.getSimpleName(), context);
		ServiceLoader<PersistenceBackend> serviceLoader = ServiceLoader.load(PersistenceBackend.class);
		for (PersistenceBackend found : serviceLoader) {
			Class<? extends PersistenceBackend> foundClass = found.getClass();
			try {
				String foundClassName = foundClass.getSimpleName();
				logger.debug("Testing {}", foundClassName);
				
				PersistenceBackendConfiguration configuration = PersistenceBackendConfiguration.getInstance(foundClass);
				if(configuration==null){
					continue;
				}
				found.prepareConnection(configuration);
				
				logger.debug("{} will be used.", foundClassName);
				found.setAggregationScheduler(AggregationScheduler.newInstance(new DefaultPersitenceExecutor(found)));
				found.setFallback(createFallback(context));
				return found;
			} catch (Exception e) {
				logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.", foundClass.getSimpleName()), e);
			}
		}
		return null;
	};
	
	protected static PersistenceBackend rediscoverPersistenceBackend(PersistenceBackend actual, String context){
		context = sanitizeContext(context);
		Long now = Calendar.getInstance().getTimeInMillis();
		Long lastCheckTimestamp = fallbackLastCheck.get(context);
		logger.debug("Last check for context {} was {}", context, lastCheckTimestamp);
		boolean myTurn = false;
		synchronized (persistenceBackends) {
			if( (lastCheckTimestamp + FALLBACK_RETRY_TIME) <= now ){
				logger.debug("The {} for context {} is {}. Is time to rediscover if there is another possibility.",
					PersistenceBackend.class.getSimpleName(), context, actual.getClass().getSimpleName());
				logger.trace("Renewing Last check Timestamp. The next one will be {}", now);
				fallbackLastCheck.put(context, now);
				myTurn=true;
				logger.debug("I win. It is my turn to rediscover {} in context {}", 
						PersistenceBackend.class.getSimpleName(), context);
			}
		}
		
		if(myTurn){
			PersistenceBackend discoveredPersistenceBackend = discoverPersistenceBackend(context);
			
			synchronized (persistenceBackends) {
				if(discoveredPersistenceBackend!=null){
					/*
					 * Passing the aggregator to the new PersistenceBackend
					 * so that the buffered records will be persisted with the 
					 * new method
					 * 
					 */
					discoveredPersistenceBackend.setAggregationScheduler(actual.getAggregationScheduler());
					
					// Removing timestamp which is no more needed
					fallbackLastCheck.remove(context);
					persistenceBackends.put(context, discoveredPersistenceBackend);
					
					/* 
					 * Not needed because close has no effect. Removed to 
					 * prevent problem in cases of future changes.
					 * try {
					 * 	actual.close();
					 * } catch (Exception e) {
					 * 	logger.error("Error closing {} for scope {} which has been substituted with {}.", 
					 * 		actual.getClass().getSimpleName(), scope,
					 * 		discoveredPersistenceBackend.getClass().getSimpleName(), e);
					 * }
					 * 
					 */
					return discoveredPersistenceBackend;
				}
			}

		}
		
		long nextCheck = (lastCheckTimestamp + FALLBACK_RETRY_TIME) - Calendar.getInstance().getTimeInMillis();
		float nextCheckInSec = nextCheck/1000;
		logger.debug("The {} for context {} is going to be used is {}. Next retry in {} msec (about {} sec)",
				PersistenceBackend.class.getSimpleName(), context, 
				actual.getClass().getSimpleName(), nextCheck, nextCheckInSec);
		
		return actual;
	}
	
	public static PersistenceBackend getPersistenceBackend(String context) {
		context = sanitizeContext(context);

		PersistenceBackend persistence = null;
		logger.debug("Going to synchronized block in getPersistenceBackend");
		synchronized (persistenceBackends) {
			persistence = persistenceBackends.get(context);
			logger.debug("{} {}", PersistenceBackend.class.getSimpleName(), persistence);
			if(persistence==null){
				/* 
				 * Setting FallbackPersistence and unlocking.
				 * This is used to avoid deadlock on IS node which try to use 
				 * itself to query configuration.
				 */
				persistence = createFallback(context);
				persistenceBackends.put(context, persistence);
				long now = Calendar.getInstance().getTimeInMillis();
				/* The PersistenceBackend is still to be discovered
				 * setting the last check advanced in time to force rediscover.
				 */
				fallbackLastCheck.put(context, ((now - FALLBACK_RETRY_TIME) - 1));
			}
		}
		
		if(persistence instanceof FallbackPersistenceBackend){
			persistence = rediscoverPersistenceBackend(persistence, context);
		}
		
		return persistence;
	}
	
	public static void flush(String context, long timeout, TimeUnit timeUnit){
		context = sanitizeContext(context);
		PersistenceBackend apb = persistenceBackends.get(context);
		try {
			logger.debug("Flushing records in context {}", context);
			apb.flush(timeout, timeUnit);
		}catch(Exception e){
			logger.error("Unable to flush records in context {} with {}", context, apb, e);
		}
	}

	/**
	 * @param timeout
	 * @param timeUnit
	 * @throws Exception 
	 */
	public static void flushAll(long timeout, TimeUnit timeUnit) {
		for(String context : persistenceBackends.keySet()){
			flush(context, timeout, timeUnit);
		}
	}
	
	
}
