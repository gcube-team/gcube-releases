package org.gcube.vremanagement.executor.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartExecutorSchedulerFactory {

	private static Logger logger = LoggerFactory.getLogger(SmartExecutorScheduler.class);
	
	private static Map<String, SmartExecutorScheduler> smartExecutorSchedulers = new HashMap<>();

	protected static SchedulerFactory schedulerFactory;
	
	static {
		schedulerFactory = new StdSchedulerFactory();
		smartExecutorSchedulers = new HashMap<>();
	}
	
	private static SmartExecutorScheduler getSmartExecutorScheduler(String scope) throws SchedulerException {
		if(scope==null){
			String error = "No Scope available.";
			logger.error(error);
			throw new RuntimeException(error); 
		}

		logger.trace("Retrieving {} for scope {}", 
				SmartExecutorPersistenceConnector.class.getSimpleName(), scope);
		
		SmartExecutorScheduler smartExecutorScheduler = smartExecutorSchedulers.get(scope);
		
		if(smartExecutorScheduler==null){
			logger.trace("Retrieving {} for scope {} not found on internal {}. Intializing it.", 
					SmartExecutorScheduler.class.getSimpleName(), 
					scope, Map.class.getSimpleName());
			
			Scheduler scheduler = schedulerFactory.getScheduler();
			smartExecutorScheduler = new SmartExecutorScheduler(scheduler);
			
			smartExecutorSchedulers.put(SmartExecutorInitializator.getCurrentScope(), 
					smartExecutorScheduler);
		}
		
		return smartExecutorScheduler;
	}
	
	/**
	 * @return the persistenceConnector
	 * @throws SchedulerException 
	 */
	public static synchronized SmartExecutorScheduler getSmartExecutorScheduler() throws SchedulerException {
		String scope = SmartExecutorInitializator.getCurrentScope();
		return getSmartExecutorScheduler(scope);
	}
	
	
	public static void remove(){
		String scope = SmartExecutorInitializator.getCurrentScope();
		smartExecutorSchedulers.remove(scope);
	}
	
}
