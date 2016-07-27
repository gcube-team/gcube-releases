/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.records.RecordUtility;
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
	
	public static final long INITIAL_DELAY = 1000; // 1 min
	public static final long FALLBACK_RETRY_TIME = 1000*60*10; // 10 min
	
	static {
		persistenceBackends = new HashMap<String, PersistenceBackend>();
	}
	
	public static void addRecordPackage(Package packageObject) {
		RecordUtility.addRecordPackage(packageObject);
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
		logger.trace("getFallbackFile location:"+getFallbackLocation()+" context:"+slashLessContext+"-"+FALLBACK_FILENAME);
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
				logger.trace("Testing {}", foundClassName);
				
				PersistenceBackendConfiguration configuration = PersistenceBackendConfiguration.getInstance(foundClass);
				if(configuration==null){
					continue;
				}
				found.prepareConnection(configuration);
				
				logger.trace("{} will be used.", foundClassName);
				
				found.setAggregationScheduler(AggregationScheduler.newInstance(new DefaultPersitenceExecutor(found),configuration));
				found.setFallback(createFallback(context));
				
				return found;
			} 
			catch (Exception e) {
				logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.", foundClass.getSimpleName()), e);
			}
		}
		return null;
	};
	
	
	public static PersistenceBackend getPersistenceBackend(String context) {
		context = sanitizeContext(context);

		PersistenceBackend persistence = null;
		logger.trace("Going to synchronized block in getPersistenceBackend");
		synchronized (persistenceBackends) {
			persistence = persistenceBackends.get(context);
			logger.trace("{} {}", PersistenceBackend.class.getSimpleName(), persistence);
			if(persistence==null){
				/* 
				 * Setting FallbackPersistence and unlocking.
				 * There will be another thread which will try to discover the 
				 * real persistence.
				 */
				persistence = createFallback(context);
				persistenceBackends.put(context, persistence);
				
				new PersistenceBackendRediscover(context,
						(FallbackPersistenceBackend) persistence, INITIAL_DELAY, 
						FALLBACK_RETRY_TIME, TimeUnit.MILLISECONDS);
			}
		}
		
		return persistence;
	}
	
	
	protected static PersistenceBackend rediscoverPersistenceBackend(PersistenceBackend actual, String context){
		context = sanitizeContext(context);
		logger.debug("The {} for context {} is {}. "
				+ "Is time to rediscover if there is another possibility.",
				PersistenceBackend.class.getSimpleName(), context, 
				actual.getClass().getSimpleName());
		
		PersistenceBackend discoveredPersistenceBackend = 
				PersistenceBackendFactory.discoverPersistenceBackend(context);
		
		if(discoveredPersistenceBackend!=null){
			synchronized (persistenceBackends) {
			
				/*
				 * Passing the aggregator to the new PersistenceBackend
				 * so that the buffered records will be persisted with the 
				 * new method
				 * 
				 */
				discoveredPersistenceBackend.setAggregationScheduler(actual.getAggregationScheduler());
				
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
		
		return actual;
	}
	
	public static void flush(String context, long timeout, TimeUnit timeUnit){
		context = sanitizeContext(context);
		
		PersistenceBackend apb;
		synchronized (persistenceBackends) {
			apb = persistenceBackends.get(context);
		}
		
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
