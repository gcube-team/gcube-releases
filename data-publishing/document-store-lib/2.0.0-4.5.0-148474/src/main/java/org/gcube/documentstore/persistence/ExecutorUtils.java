package org.gcube.documentstore.persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ExecutorUtils {

	public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(50, new ThreadFactory() {
		
		private int counter = 0;
		private static final String prefix = "AccountingScheduledThread";	 

		public Thread newThread(Runnable r) {
		   return new Thread(r, prefix + "-" + counter++);
		}
	});
	
	public static ExecutorService threadPool = Executors.newFixedThreadPool(100, new ThreadFactory() {
		
		private int counter = 0;
		private static final String prefix = "AccountingAggregationThread";

		public Thread newThread(Runnable r) {
		   return new Thread(r, prefix + "-" + counter++);
		}
	});
	
	
	
}
