package org.gcube.documentstore.persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ExecutorUtils {

	public static final ScheduledExecutorService PERSISTENCE_BACKEND_REDISCOVERY_POOL;
	public static final ScheduledExecutorService CONFIGURATION_REDISCOVERY_POOL;
	
	public static final ScheduledExecutorService FUTURE_FLUSH_POOL;
	public static final ScheduledExecutorService FALLBACK_ELABORATOR_POOL;
	
	
	public static final ExecutorService ASYNC_AGGREGATION_POOL;
	
	static {
		
		PERSISTENCE_BACKEND_REDISCOVERY_POOL = Executors.newScheduledThreadPool(20, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "PersistenceBackendRediscoveryThread";	 
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
		
		CONFIGURATION_REDISCOVERY_POOL = Executors.newScheduledThreadPool(20, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "ConfigurationRediscoveryThread";	 
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
		FUTURE_FLUSH_POOL = Executors.newScheduledThreadPool(20, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "FlushThread";	 
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
		FALLBACK_ELABORATOR_POOL = Executors.newScheduledThreadPool(20, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "FallbackElaboratorThread";	 
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
		
		ASYNC_AGGREGATION_POOL = Executors.newFixedThreadPool(30, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "AsyncAggregationThread";
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
	}
	
	
}
