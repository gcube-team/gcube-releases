/**
 * 
 */
package org.gcube.vremanagement.executor.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.exception.SchedulerNotFoundException;
import org.gcube.vremanagement.executor.exception.SchedulerRemoveException;
import org.gcube.vremanagement.executor.exception.UnableToInterruptTaskException;
import org.gcube.vremanagement.executor.pluginmanager.PluginManager;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTaskPersistence;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTaskPersistenceFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SmartExecutorScheduler {

	private static Logger logger = LoggerFactory
			.getLogger(SmartExecutorScheduler.class);

	/**
	 * Contains running plugin instances. The key is the associated random UUID.
	 * This is needed to correctly stop the running plugin execution if the
	 * container is stopped in the proper way
	 */
	protected Map<UUID, Scheduler> activeSchedulers;

	private static SmartExecutorScheduler smartExecutorScheduler;

	public synchronized static SmartExecutorScheduler getInstance() {
		if (smartExecutorScheduler == null) {
			smartExecutorScheduler = new SmartExecutorScheduler();
		}
		return smartExecutorScheduler;
	}

	private SmartExecutorScheduler() {
		activeSchedulers = new HashMap<UUID, Scheduler>();
	}
	
	
	protected TriggerBuilder<? extends Trigger> createTriggerBuilder(UUID uuid, ScheduleBuilder<? extends Trigger> sb){
		return TriggerBuilder.newTrigger().withIdentity(uuid.toString())
				.withSchedule(sb);
	}
	
	protected TriggerBuilder<? extends Trigger> getTriggerBuilderWithScheduling(UUID uuid, Scheduling scheduling) throws LaunchException{
		
		final int times = scheduling.getSchedulingTimes();
		
		if (scheduling.getCronExpression() != null) {
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
					.cronSchedule(scheduling.getCronExpression());
			
			return createTriggerBuilder(uuid, cronScheduleBuilder);
		}
		
		if (scheduling.getDelay() != null) {
			SimpleScheduleBuilder simpleScheduleBuilder; 
			
			if (times != 0) {
				simpleScheduleBuilder = SimpleScheduleBuilder
						.repeatSecondlyForTotalCount(times, scheduling.getDelay());
			}else{
				simpleScheduleBuilder = SimpleScheduleBuilder.
						repeatSecondlyForever(scheduling.getDelay());
			}
			
			return createTriggerBuilder(uuid, simpleScheduleBuilder);
		}

		throw new LaunchException("Invalid Scheduling");
		
	}
	
	/**
	 * Create the Scheduler using the strategy provided by LaunchParameter
	 * @param uuid the UUID will be used to identify the task
	 * @param parameter LaunchParameter requested in service invocation
	 * @return the created scheduler
	 * @throws LaunchException if the LaunchParameter does not contains a valid 
	 * scheduling strategy
	 * @throws SchedulerException if the scheduler cannot be created by the 
	 * scheduler factory
	 */
	protected Scheduler reallySchedule(final UUID uuid,  LaunchParameter parameter) throws LaunchException, SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		JobKey jobKey = new JobKey(uuid.toString());
		JobDetail jobDetail = JobBuilder.newJob(SmartExecutorTask.class).
				withIdentity(jobKey).build();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put(SmartExecutorTask.UUID, uuid);
		jobDataMap.put(SmartExecutorTask.LAUNCH_PARAMETER, parameter);
		
		@SuppressWarnings("rawtypes")
		TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
				.withIdentity(uuid.toString());

		Scheduling scheduling = parameter.getScheduling();
		
		if (scheduling != null) {
			
			triggerBuilder = getTriggerBuilderWithScheduling(uuid, scheduling);
			
			if (scheduling.getFirstStartTime() != null && scheduling.getFirstStartTime().longValue()!=0) {
				Date triggerStartTime = new Date(scheduling.getFirstStartTime());
				triggerBuilder.startAt(triggerStartTime);
			} else {
				triggerBuilder.startNow();
				scheduling.setFirstStartTime(Calendar.getInstance().getTimeInMillis());
			}
			
			if (scheduling.getEndTime() != null && scheduling.getEndTime().longValue()!=0) {
				Date triggerEndTime = new Date(scheduling.getEndTime());
				triggerBuilder.endAt(triggerEndTime);
			}
			
			try {
				ScheduledTaskPersistence stc = ScheduledTaskPersistenceFactory.getScheduledTaskPersistence();
				ScheduledTask scheduledTask = new ScheduledTask(uuid, parameter);
				logger.debug("Going to persist Scheduled Task {} : {} ", 
						scheduledTask);
				stc.addScheduledTask(scheduledTask);
			} catch (Exception e) {
				logger.error("Unable to persist Scheduled Task {}", uuid.toString(), e.getCause());
			} 
			
		} else {
			triggerBuilder.startNow();
		}
		
		try {
			SmartExecutorTaskListener sejl = new SmartExecutorTaskListener();
			scheduler.getListenerManager().addJobListener(sejl);
			scheduler.scheduleJob(jobDetail, triggerBuilder.build());
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		
		return scheduler;
	}
	
	/**
	 * Schedule a task execution 
	 * @param parameter LaunchParameter requested in service invocation
	 * @return the UUID which will identify the task
	 * @throws LaunchException if the LaunchParameter does not contains a valid 
	 * scheduling strategy
	 * @throws InputsNullException if provided input map is null
	 * @throws PluginNotFoundException if the request plugin is not available on
	 * this smart executor instance
	 */
	public synchronized UUID schedule(LaunchParameter parameter, UUID uuid) 
			throws InputsNullException, PluginNotFoundException, LaunchException {
		Map<String, Object> inputs = parameter.getInputs();
		if (inputs == null) {
			throw new InputsNullException();
		}
		
		/*
		 * Checking if the requested plugin is available on this smart executor
		 * instance
		 */
		PluginManager.getPluginDeclaration(parameter.getPluginName());
		
		if(uuid==null){
			uuid = UUID.randomUUID();
		}
		
		try {
			Scheduler scheduler = reallySchedule(uuid, parameter);
			activeSchedulers.put(uuid, scheduler);
			scheduler.start();
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		
		return uuid;
	}
	
	public Scheduler getScheduler(UUID key){
		return activeSchedulers.get(key);
	}

	protected void stopLastcurrentExecution(Scheduler scheduler, UUID uuid) 
			throws UnableToInterruptTaskException{
		
		JobKey jobKey = new JobKey(uuid.toString());
		
		try {
			logger.debug("Going to stop current SmartExecutor Task {} execution if any", uuid);
			if(!scheduler.checkExists(jobKey)){
				logger.debug("No SmartExecutor Task {} was found. That's all folk.", uuid);
				throw new SchedulerNotFoundException("Scheduler Not Found");
			}
			
			boolean interrupted = scheduler.interrupt(jobKey);
			if (interrupted) {
				logger.debug("SmartExecutor Task {} interrupted successfully.", uuid);
			} else {
				List<JobExecutionContext> list = getCurrentlyExecutingJobs(scheduler);
				if(list!=null && list.size()>0){
					logger.debug("SmartExecutor Task {} was not interrupted.", uuid);
					throw new UnableToInterruptTaskException(uuid);
				}
			}
		} catch (UnableToInterruptTaskException e) {
			throw e;
		} catch(Exception e){
			throw new UnableToInterruptTaskException(uuid, e);
		}
	}
	
	
	protected void deleteScheduler(Scheduler scheduler, UUID uuid) throws SchedulerRemoveException {
		
		JobKey jobKey = new JobKey(uuid.toString());
		
		try {
			logger.debug("Going to delete SmartExecutor Scheduled Task {}", uuid);
			boolean deleted = scheduler.deleteJob(jobKey);
			if (deleted) {
				logger.debug("SmartExecutor Task {} deleted successfully", uuid);
			} else {
				logger.debug("SmartExecutor Task {} was not deleted", uuid);
				throw new SchedulerRemoveException(uuid);
			}
		} catch(SchedulerRemoveException e){
			throw e;
		} catch(Exception e1){
			throw new SchedulerRemoveException(uuid, e1);
		} finally {
			activeSchedulers.remove(uuid);
			try {
				scheduler.clear();
			} catch(SchedulerException e){
				throw new SchedulerRemoveException(uuid, e);
			}
		}
	}
	
	protected List<JobExecutionContext> getCurrentlyExecutingJobs(Scheduler scheduler) throws SchedulerException{
		logger.trace("Getting {} list", JobExecutionContext.class.getSimpleName());
		List<JobExecutionContext> cej = scheduler.getCurrentlyExecutingJobs();
		logger.trace("{} list got {}", JobExecutionContext.class.getSimpleName(), cej);
		return cej;
	}
	
	public LaunchParameter getLaunchParameter(Scheduler scheduler, JobKey jobKey) throws SchedulerException{
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		return (LaunchParameter) jobDataMap.get(SmartExecutorTask.LAUNCH_PARAMETER);
	}
	
	
	protected void removeFromPersistence(boolean global, UUID uuid, boolean remove) throws SchedulePersistenceException{
		try {
			ScheduledTaskPersistence stc = ScheduledTaskPersistenceFactory.getScheduledTaskPersistence();
			if(remove){
				logger.debug("Going to remove the SmartExecutor Scheduled Task {} from global scheduling", uuid);
				stc.removeScheduledTask(uuid);
			}else{
				if(global){
					logger.debug("Going to release the SmartExecutor Scheduled Task {}. The Task can be take in charge from another SmartExecutor instance", uuid);
					stc.releaseScheduledTask(uuid);
				}else{
					logger.debug("Going to remove the SmartExecutor Scheduled Task {} from local scheduling", uuid);
					stc.removeScheduledTask(uuid);
				}
			}
		}catch(Exception e){
			throw new SchedulePersistenceException(
					String.format("Unable to Remove Scheduled Task %s from global scheduling", 
							uuid.toString()), e);
		}
	}
	
	/**
	 * Stop the execution of the Task identified by UUID 
	 * @param uuid which identify the Task
	 * @param stopOnly 
	 * @param remove : when the Task is a Scheduled one indicate if the Task
	 * has to be released or to be removed (the argument is set to true when
	 * an explicit request arrive to remove the scheduled task)
	 * @throws UnableToInterruptTaskException
	 * @throws SchedulerRemoveException
	 * @throws SchedulePersistenceException
	 * @throws SchedulerNotFoundException
	 * @throws SchedulerException
	 */
	public synchronized void stop(UUID uuid, boolean stopOnly, boolean remove) 
			throws UnableToInterruptTaskException, SchedulerRemoveException,
			SchedulePersistenceException, SchedulerException {
		
		Scheduler scheduler = activeSchedulers.get(uuid);
		if(scheduler==null){
			logger.debug("No SmartExecutor Task {} was found. That's all folk.", uuid);
			removeFromPersistence(true, uuid, remove);
			return;
		}
		
		JobKey jobKey = new JobKey(uuid.toString());
		boolean exist = scheduler.checkExists(jobKey);
		if(!exist){
			logger.trace("SmartExecutor Task {} does not have any instaces associated. Cleaning the environment. That's all folk.", uuid);
			activeSchedulers.remove(uuid);
			return;
		}else{
			logger.trace("SmartExecutor Task {} to stop exist", uuid);
		}
		
		LaunchParameter launchParameter = getLaunchParameter(scheduler, jobKey);
		Scheduling scheduling = launchParameter.getScheduling();
		boolean scheduled = launchParameter.getScheduling() != null ? true : false;
		
		stopLastcurrentExecution(scheduler, uuid);
		
		try {
			if(stopOnly ^ scheduled){
				deleteScheduler(scheduler, uuid);
			}
		}catch(Exception e){
			throw e;
		} finally {
			if(!stopOnly && scheduled){
				/* Removing scheduling from persistence */
				removeFromPersistence(scheduling.getGlobal(), uuid, remove);
			}
		}
		
	}

	public void stopAll() {
		List<UUID> set = new ArrayList<UUID>(activeSchedulers.keySet());
		for (UUID uuid : set) {
			try {
				stop(uuid, true, false);
			} catch (Exception e) {
				logger.error("Error stopping plugin instace with UUID {}",
						uuid, e);
			}
		}
	}
	
}
