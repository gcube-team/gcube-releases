package org.gcube.vremanagement.executor;

import java.util.UUID;

import javax.jws.WebService;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.exception.SchedulerNotFoundException;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorScheduler;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorSchedulerFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Effective implementation of Executor
 * @author Luca Frosini (ISTI - CNR)
 */
@WebService(
portName = "SmartExecutorPort",
serviceName = SmartExecutor.WEB_SERVICE_SERVICE_NAME, 
targetNamespace = SmartExecutor.TARGET_NAMESPACE,
endpointInterface = "org.gcube.vremanagement.executor.api.SmartExecutor" )
@ManagedBy(SmartExecutorInitializator.class)
public class SmartExecutorImpl implements SmartExecutor {
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorImpl.class);
	
	/**{@inheritDoc}*/
	@Override
	public String launch(LaunchParameter parameter) throws InputsNullException, 
		PluginNotFoundException, LaunchException, ExecutorException {
		
		logger.info("Launch requested {}", parameter);
		
		SmartExecutorScheduler smartExecutorScheduler;
		try {
			smartExecutorScheduler = SmartExecutorSchedulerFactory.getSmartExecutorScheduler();
		} catch (SchedulerException e) {
			throw new ExecutorException(e);
		}
		UUID uuid = smartExecutorScheduler.schedule(parameter, null);
		logger.info(
				String.format(
						"The Plugin named %s with UUID %s has been launched %s", 
						parameter.getPluginName(), uuid.toString(), parameter));
		
		return uuid.toString();
	}
	
	/**{@inheritDoc}*/
	@Override
	public boolean stop(String executionIdentifier) throws ExecutorException {
		logger.info("Receinved stop() for {}", executionIdentifier);
		boolean ret = unSchedule(executionIdentifier, false);
		logger.info("{} was{} stopped successfully", executionIdentifier, ret? "" : " NOT");
		return ret;
	}
	
	/**{@inheritDoc}*/
	@Override
	public boolean unSchedule(String executionIdentifier, boolean globally)
			throws ExecutorException {
		logger.info("UnSchedule requested for {} globally : {}", 
				executionIdentifier, globally);
		boolean ret = reallyUnSchedule(executionIdentifier, globally);
		logger.info("{} was{} unscheduled {} successfully", executionIdentifier, ret? "" : " NOT", globally? "globally": "locally");
		return ret;
	}

	// TODO Manage better exception to to advise the caller
	protected boolean reallyUnSchedule(String executionIdentifier, boolean globally) throws ExecutorException {
		boolean currentStopped = true;
		try {
			SmartExecutorScheduler smartExecutorScheduler = SmartExecutorSchedulerFactory.getSmartExecutorScheduler();
			UUID uuid  = UUID.fromString(executionIdentifier);
			smartExecutorScheduler.stop(uuid, globally);
		} catch (SchedulerNotFoundException e) {
			// currentStopped = true;
			logger.error("Error unscheduling task {}", executionIdentifier, e);
			throw new ExecutorException(e);
		} catch(SchedulerException e){
			// currentStopped = false;
			logger.error("Error unscheduling task {}", executionIdentifier, e);
			throw new ExecutorException(e);
		} catch(SchedulePersistenceException e){
			// currentStopped = true;
			logger.error("Error removing scheduled task from persistence.", e);
		} catch (Exception e) {
			// currentStopped = false;
			logger.error("Error unscheduling task {}", executionIdentifier, e);
			throw new ExecutorException(e);
		}
		return currentStopped;
	}
	
	/**{@inheritDoc}*/
	@Override
	@Deprecated
	public PluginState getState(String executionIdentifier)  
			throws PluginInstanceNotFoundException, ExecutorException {
		return getStateEvolution(executionIdentifier).getPluginState();
	}
	
	/**{@inheritDoc}*/
	@Override
	public PluginStateEvolution getStateEvolution(String executionIdentifier) 
			throws PluginInstanceNotFoundException, ExecutorException {
		logger.info("getStateEvolution() requested for {}", executionIdentifier);
		try {
			SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
			PluginStateEvolution pluginStateEvolution = persistenceConnector.getLastPluginInstanceState(UUID.fromString(executionIdentifier));
			logger.info("getState() for {} is : {}", executionIdentifier, pluginStateEvolution);
			return pluginStateEvolution;
		} catch (Exception e) {
			throw new PluginInstanceNotFoundException(e);
		}
	}
	
	/**{@inheritDoc}*/
	@Override
	@Deprecated
	public PluginState getIterationState(String executionIdentifier, int iterationNumber) 
			throws PluginInstanceNotFoundException, ExecutorException {
		return getIterationStateEvolution(executionIdentifier, iterationNumber).getPluginState();
	}
	
	/**{@inheritDoc}*/
	@Override
	public PluginStateEvolution getIterationStateEvolution(String executionIdentifier, int iterationNumber) 
			throws PluginInstanceNotFoundException, ExecutorException {
		logger.info("getIterationStateEvolution() requested for {} (iteration n. {})", executionIdentifier, iterationNumber);
		try {
			SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
			PluginStateEvolution pluginStateEvolution = persistenceConnector.getPluginInstanceState(UUID.fromString(executionIdentifier), iterationNumber);
			logger.info("getIterationState() for {} (iteration n. {}) is : {}", executionIdentifier, iterationNumber, pluginStateEvolution);
			return pluginStateEvolution;
		} catch (Exception e) {
			throw new PluginInstanceNotFoundException(e);
		}
	}
	
	
}
