/**
 * 
 */
package org.gcube.vremanagement.executor.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.exception.SchedulerNotFoundException;
import org.gcube.vremanagement.executor.exception.UnableToInterruptTaskException;
import org.gcube.vremanagement.executor.json.ObjectMapperManager;
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
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SmartExecutorScheduler {

	private static Logger logger = LoggerFactory
			.getLogger(SmartExecutorScheduler.class);

	
	protected Set<UUID> scheduledJobs;
	protected final Scheduler shedul;
	
	SmartExecutorScheduler(Scheduler scheduler) throws SchedulerException {
		this.shedul = scheduler;
		this.shedul.start();
		this.scheduledJobs = new HashSet<>();
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
	protected void reallySchedule(final UUID uuid,  LaunchParameter parameter) throws LaunchException, SchedulerException {
		
		JobKey jobKey = new JobKey(uuid.toString());
		JobDetail jobDetail = JobBuilder.newJob(SmartExecutorTask.class).
				withIdentity(jobKey).build();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put(SmartExecutorTask.UUID, uuid);
		jobDataMap.put(SmartExecutorTask.LAUNCH_PARAMETER, parameter);
		
		String token = SecurityTokenProvider.instance.get();
		jobDataMap.put(SmartExecutorTask.TOKEN, token);
		
		@SuppressWarnings("rawtypes")
		TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
				.withIdentity(uuid.toString());

		Scheduling scheduling = parameter.getScheduling();
		
		if (scheduling != null) {
			
			try {
				logger.info("Going to schedule Taks with UUID with the following {} : {}", uuid, LaunchParameter.class.getSimpleName(), ObjectMapperManager.getObjectMapper().writeValueAsString(parameter));
			} catch (Exception e) {
				
			}
			
			triggerBuilder = getTriggerBuilderWithScheduling(uuid, scheduling);
			
			if (scheduling.getFirstStartTime() != null && scheduling.getFirstStartTime().longValue()!=0) {
				Date triggerStartTime = new Date(scheduling.getFirstStartTime());
				triggerBuilder.startAt(triggerStartTime);
			} 
			
			/*
			else {
				triggerBuilder.startNow();
				scheduling.setFirstStartTime(Calendar.getInstance().getTimeInMillis());
			}
			*/
			
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
			try {
				logger.info("Starting Taks with UUID {} immediately with the following {} : {}", uuid, LaunchParameter.class.getSimpleName(), ObjectMapperManager.getObjectMapper().writeValueAsString(parameter));
			} catch (Exception e) {
				
			}
			triggerBuilder.startNow();
		}
		
		try {
			SmartExecutorTaskListener sejl = new SmartExecutorTaskListener();
			shedul.getListenerManager().addJobListener(sejl);
			shedul.scheduleJob(jobDetail, triggerBuilder.build());
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
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
			reallySchedule(uuid, parameter);
			scheduledJobs.add(uuid);
		} catch (SchedulerException e) {
			throw new LaunchException(e);
		}
		
		return uuid;
	}

	protected void stopTask(UUID uuid) 
			throws UnableToInterruptTaskException{
		
		JobKey jobKey = new JobKey(uuid.toString());
		
		try {
			logger.debug("Going to stop current SmartExecutor Task {} execution if any", uuid);
			if(!shedul.checkExists(jobKey)){
				logger.trace("SmartExecutor Task {} does not have any instaces associated. Cleaning the environment. That's all folk.", uuid);
				scheduledJobs.remove(uuid);
				throw new SchedulerNotFoundException("Scheduler Not Found");
			}
			
			boolean interrupted = shedul.interrupt(jobKey);
			shedul.deleteJob(jobKey);
			if (interrupted) {
				logger.debug("SmartExecutor Task {} interrupted successfully.", uuid);
			} else {
				logger.debug("SmartExecutor Task {} was not interrupted.", uuid);
			}
			
		} catch(Exception e){
			throw new UnableToInterruptTaskException(uuid, e);
		} 
	}
	
	protected List<JobExecutionContext> getCurrentlyExecutingJobs(Scheduler scheduler) throws SchedulerException{
		logger.trace("Getting {} list", JobExecutionContext.class.getSimpleName());
		List<JobExecutionContext> cej = scheduler.getCurrentlyExecutingJobs();
		logger.trace("{} list got {}", JobExecutionContext.class.getSimpleName(), cej);
		return cej;
	}
	
	public LaunchParameter getLaunchParameter(JobKey jobKey) throws SchedulerException{
		JobDetail jobDetail = shedul.getJobDetail(jobKey);
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		return (LaunchParameter) jobDataMap.get(SmartExecutorTask.LAUNCH_PARAMETER);
	}
	
	
	/**
	 * Stop the execution of the Task identified by UUID 
	 * @param uuid which identify the Task
	 * @param stopOnly 
	 * @param remove : when the Task is a Scheduled one indicate if the Task
	 * has to be released or to be removed (the argument is set to true when
	 * an explicit request arrive to remove the scheduled task)
	 * @throws Exception 
	 * @throws SchedulerNotFoundException
	 */
	public synchronized void stop(UUID uuid, boolean remove) 
			throws Exception {
		
		JobKey jobKey = new JobKey(uuid.toString());
		LaunchParameter launchParameter = getLaunchParameter(jobKey);
		Scheduling scheduling = launchParameter.getScheduling();
		boolean scheduled = scheduling != null ? true : false;
		
		stopTask(uuid);
		
		ScheduledTaskPersistence stc = ScheduledTaskPersistenceFactory.getScheduledTaskPersistence();
		
		if(scheduled){
			if(remove){
				logger.debug("Going to remove the SmartExecutor Scheduled Task {} from global scheduling", uuid);
				stc.removeScheduledTask(uuid);
			}else{
				if(scheduling.getGlobal()){
					logger.debug("Going to release the SmartExecutor Scheduled Task {}. The Task can be take in charge from another SmartExecutor instance", uuid);
					stc.releaseScheduledTask(uuid);
				}
			}
		}
		
	}

	public void stopAll() {
		List<UUID> set = new ArrayList<UUID>(scheduledJobs);
		for (UUID uuid : set) {
			try {
				stop(uuid, false);
			} catch (Exception e) {
				logger.error("Error stopping plugin instace with UUID {}",
						uuid, e);
			}
		}
	}
	
}
