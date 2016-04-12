package org.gcube.application.aquamaps.aquamapsservice.impl.util;



import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyPooledExecutor extends ThreadPoolExecutor implements ExtendedExecutor{

	final static Logger logger= LoggerFactory.getLogger(MyPooledExecutor.class);
	
	/**
	 * Uses java.util.concurrent.Executors.defaultThreadFactory() setting threadLabel and priority (optional)
	 * 
	 * @author fabio
	 *
	 */
	

	protected static class MyThreadFactory implements ThreadFactory{
		
		private String label;
		private int priority;
		private boolean setPriority;
		private int index=0;
		
		public MyThreadFactory(String threadLabel,int priority) {
			super();			
			this.label=threadLabel;
			this.priority=priority;
			setPriority=true;
		}
		public MyThreadFactory(String threadLabel){
			super();
			this.label=threadLabel;
			setPriority=false;
		}
		
		@Override
		public Thread newThread(Runnable arg0) {			
			Thread toReturn=Executors.defaultThreadFactory().newThread(arg0);
//			toReturn.setName(ServiceUtils.generateId(label, ""));
			toReturn.setName(label+index);
			index++;
			if(setPriority)toReturn.setPriority(priority);
			return toReturn;
		}
	}
	
	private MyPooledExecutor(final String threadLabel,int maxThread){
		super(maxThread,
				maxThread, 
				Long.MAX_VALUE, 
				TimeUnit.MILLISECONDS, 
				new ArrayBlockingQueue<Runnable>(maxThread*2),  
				new MyThreadFactory(threadLabel), 
//				new RejectedExecutionHandler() {
//					
//					@Override
//					public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//						try {
//							System.out.println("Request queue for  "+threadLabel+" full, blocking request. Pool stats : "+((ExtendedExecutor)e).getDetails());
//							e.getQueue().put(r);
//							System.out.println("Request queue for  "+threadLabel+" no more full, request queued. Pool stats : "+((ExtendedExecutor)e).getDetails());
//						} catch (InterruptedException e1) {
//							 logger.warn("Work discarded because thread was interrupted while waiting to schedule: " + r);
//						}
//					}
//				}
				new ThreadPoolExecutor.CallerRunsPolicy()
		);
	}
	
	
	@Override
	public String getDetails() {
		StringBuilder statusBuilder=new StringBuilder();
		statusBuilder.append("Active Count : "+getActiveCount()+";");
		statusBuilder.append("Core Size : "+getCorePoolSize()+";");
		statusBuilder.append("Maximum pool size: "+getMaximumPoolSize()+";");
		statusBuilder.append("Current Queue size: "+getQueue().size()+";");
		statusBuilder.append("Largest pool size : "+getLargestPoolSize()+";");
		statusBuilder.append("Executed Count : "+getCompletedTaskCount()+";");
		return statusBuilder.toString();
	}
	
	public static ExtendedExecutor getExecutor(String threadLabel,int maxThread){
		return new MyPooledExecutor(threadLabel, maxThread); 
	}
	

	
}
