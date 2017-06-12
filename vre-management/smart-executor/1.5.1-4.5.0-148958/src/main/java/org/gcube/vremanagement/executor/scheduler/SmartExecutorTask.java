/**
 * 
 */
package org.gcube.vremanagement.executor.scheduler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.AlreadyInFinalStateException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.gcube.vremanagement.executor.pluginmanager.PluginManager;
import org.gcube.vremanagement.executor.pluginmanager.RunnablePlugin;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class SmartExecutorTask implements InterruptableJob {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorTask.class);

	public static final String UUID = "UUID";
	public static final String LAUNCH_PARAMETER = "LAUNCH_PARAMETER";
	public static final String TOKEN = "TOKEN";
	
	protected static Map<UUID, Integer> executionsCount;
	
	/**
	 * @return the executionsCount
	 */
	public static Map<UUID, Integer> getExecutionsCount() {
		return executionsCount;
	}

	protected static Map<UUID, Map<Integer, PluginState>> executionsState;
	
	static {
		executionsCount = new HashMap<UUID, Integer>();
		executionsState = new HashMap<UUID, Map<Integer, PluginState>>();
	}
	
	protected List<PluginStateNotification> pluginStateNotifications;
	
	protected boolean initialized;
	
	protected UUID uuid;
	protected LaunchParameter launchParameter;
	protected String token;
	
	/* Derived from launchParameter*/
	protected int executionCount;
	protected String pluginName;
	protected Plugin<? extends PluginDeclaration> plugin;
	protected Map<String, Object> inputs;
	protected RunnablePlugin<Plugin<? extends PluginDeclaration>> runnablePlugin;
	protected boolean mustPreviousExecutionsCompleted;
	protected int maxExecutionNumber;
	/**/
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	protected void init(JobDataMap jobDataMap) throws JobExecutionException{
		uuid = (UUID) jobDataMap.get(UUID);
		launchParameter = (LaunchParameter) jobDataMap.get(LAUNCH_PARAMETER);
		token = (String) jobDataMap.get(TOKEN);
				
		pluginName = launchParameter.getPluginName();
		
		try {
			plugin = PluginManager.instantiatePlugin(pluginName);
		} catch (InputsNullException | PluginNotFoundException e) {
			throw new JobExecutionException(e);
		}
		
		inputs = launchParameter.getInputs();
		
		Scheduling scheduling = launchParameter.getScheduling();
		if(scheduling!=null){
			mustPreviousExecutionsCompleted = scheduling.mustPreviousExecutionsCompleted();
			
			if(mustPreviousExecutionsCompleted){
				Map<Integer, PluginState> executionState;
				if(executionsState.containsKey(uuid)){
					executionState = executionsState.get(uuid);
				}else{
					executionState = new HashMap<Integer, PluginState>();
					executionsState.put(uuid, executionState);
					executionState.put(0, PluginState.DONE);
				}
				pluginStateNotifications.add(new JobCompletedNotification(executionState));
			}
			
			maxExecutionNumber = scheduling.getSchedulingTimes();
			
			// TODO Insert code to dynamically discover notification to
			// attach and attach the requested ones.
			// pluginStateNotifications.add(...);
		}
		
		Map<String, Map<String, String>> pluginStateNotificationWithInputs = launchParameter.getPluginStateNotifications();
		if(pluginStateNotificationWithInputs!=null){
			for(String pluginStateNotificationsClassName : pluginStateNotificationWithInputs.keySet()){
				if(pluginStateNotificationsClassName.compareTo(SmartExecutorPersistenceConnector.class.getName())==0) {
					logger.warn("{} is for internal use only. It will be skypped", pluginStateNotificationsClassName);
					continue;
				}
				if(pluginStateNotificationsClassName.compareTo(JobCompletedNotification.class.getName())==0){
					logger.warn("{} is for internal use only. It will be skypped", pluginStateNotificationsClassName);
					continue;
				}
				Class<? extends PluginStateNotification> clazz;
				try {
					logger.trace("Trying to instantiate {}", pluginStateNotificationsClassName);
					clazz = (Class<? extends PluginStateNotification>) Class.forName(pluginStateNotificationsClassName);
					Constructor<?> constructor = clazz.getConstructor(Map.class);
					Map<String,String> notificationInputs = pluginStateNotificationWithInputs.get(pluginStateNotificationsClassName);
					PluginStateNotification pluginStateNotification = (PluginStateNotification) constructor.newInstance(notificationInputs);
					logger.trace("{} succesfully instantiated : {}", pluginStateNotificationsClassName, pluginStateNotification);
					pluginStateNotifications.add(pluginStateNotification);
				} catch (Exception e) {
					logger.error("Error instantiating {} : {}. It will be ignored", pluginStateNotificationsClassName, e.getMessage());
				}
				
			}
		}
		
		initialized = true;
	}
	
	protected Boolean interrupted;
	
	
	public SmartExecutorTask() throws Exception {
		this.interrupted = false;
		this.initialized = false;
		this.pluginStateNotifications = new ArrayList<PluginStateNotification>();
		this.pluginStateNotifications.add(SmartExecutorPersistenceFactory.getPersistenceConnector());
	}
	
	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @return the parameter
	 */
	public LaunchParameter getLaunchParameter() {
		return launchParameter;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		logger.debug("Execute of {}", this);
		
		synchronized(this){
			if(interrupted){
				logger.info("A job interruption has been called before that this {} has been executed for the first time", SmartExecutorTask.class.getSimpleName());
				return;
			}
			
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			logger.debug("Execute of {} with {}", this, jobDataMap);
			
			
			if(!initialized){
				init(jobDataMap);
			}
			
			if(executionsCount.containsKey(uuid)) {
				executionCount = executionsCount.get(uuid);
			} else{
				executionCount = 0;
			}
			executionCount++;
			executionsCount.put(uuid, executionCount);
			
			if(isMaxExecutionNumberReached()){
				logger.debug("The Scheduled Max Number of execution ({}) is reached. The SmartExecutor Task {} will be descheduled", maxExecutionNumber, uuid);
				try {
					deschedule(true);
				} catch (Exception e) {
					throw new JobExecutionException(e);
				}
				return;
			}
			
			runnablePlugin = new RunnablePlugin<Plugin<? extends PluginDeclaration>>(
					plugin, inputs, uuid, executionCount, pluginStateNotifications, token);
			
			logger.debug("Going to run Job with ID {} (iteration {})", uuid, executionCount);
			
		}
		
		if(mustPreviousExecutionsCompleted){
			
			Map<Integer, PluginState> executionState = executionsState.get(uuid);
			boolean previousExecutionCompleted = true;
			
			int notTerminatedExecutionNumber = -1;
			for(int i=executionCount-1; i>=0; i--){
				PluginState previousState =  executionState.get(i);
				previousExecutionCompleted = previousState.isFinalState();
				if(!previousExecutionCompleted){
					notTerminatedExecutionNumber = i;
					break;
				}
			}
			
			if(previousExecutionCompleted){
				runnablePlugin.run();
			}else{
				logger.info("A previuos execution ({}) is still not completed. The Launch Parameters require this. This execution ({}) is discarded.", 
						notTerminatedExecutionNumber, executionCount);
				try {
					runnablePlugin.setState(PluginState.DISCARDED);
				} catch (AlreadyInFinalStateException | InvalidPluginStateEvolutionException e) { }
			}
		}else{
			runnablePlugin.run();
		}
		
	}
	
	protected synchronized void finished(JobExecutionContext context){
		logger.debug("Job with ID {} (iteration {})terminated", uuid, executionCount);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void interrupt() throws UnableToInterruptJobException {
		if(!initialized){
			logger.info("{} does not need to be interrupted, because the execute method is not still called.", SmartExecutorTask.class.getSimpleName());
			interrupted = true;
			return;
		}
		
		logger.debug("Trying to interrupt {} iteration({})", uuid, executionCount);
		try {
			logger.debug("Requesting Stop to plugin instance ({}) identified by the UUID {} of Plugin named {}",
					executionCount, uuid, pluginName);
			runnablePlugin.stop();
			logger.debug("Plugin instance ({}) identified by the UUID {} of Plugin named {} stopped itself correctly.",
					executionCount, uuid, pluginName);
		} catch (Exception e) {
			logger.error("Running plugin instance ({}) identified by the UUID {} of Plugin named {} failed to request of being stopped.",
					executionCount, uuid, pluginName);
		} 
		
	}
	
	protected boolean isMaxExecutionNumberReached(){
		if(maxExecutionNumber==0){
			return false;
		}
		
		if(executionCount>maxExecutionNumber){
			return true;
		}
		
		return false;
	}
	
	protected void deschedule(boolean globally) 
			throws Exception {
		SmartExecutorSchedulerFactory.getSmartExecutorScheduler().stop(uuid, globally);
	}
	
	@Override
	public String toString(){
		if(!initialized){
			return super.toString();
		}
		return String.format("JOb with ID %s (iteration %d). Parameters : %s", uuid, executionCount, launchParameter);
	}
	
}
