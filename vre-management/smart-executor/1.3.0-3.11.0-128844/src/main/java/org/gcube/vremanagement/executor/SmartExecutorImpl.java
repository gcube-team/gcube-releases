package org.gcube.vremanagement.executor;

import java.util.UUID;

import javax.jws.WebService;

import org.gcube.smartgears.context.application.ApplicationContext;
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
import org.gcube.vremanagement.executor.scheduler.SmartExecutorScheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Effective implementation of Executor
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@WebService(
portName = "SmartExecutorPort",
serviceName = SmartExecutor.WEB_SERVICE_SERVICE_NAME, 
targetNamespace = SmartExecutor.TARGET_NAMESPACE,
endpointInterface = "org.gcube.vremanagement.executor.api.SmartExecutor" )
public class SmartExecutorImpl implements SmartExecutor {
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorImpl.class);
	
	protected static ApplicationContext ctx;
	
	/**
	 * @return the ctx
	 */
	public static ApplicationContext getCtx() {
		return ctx;
	}
	
	/**{@inheritDoc}*/
	@Override
	public String launch(LaunchParameter parameter) throws InputsNullException, 
		PluginNotFoundException, LaunchException, ExecutorException {
		
		SmartExecutorScheduler smartExecutorScheduler = SmartExecutorScheduler.getInstance();
		UUID uuid = smartExecutorScheduler.schedule(parameter);
		logger.debug(String.format("The Plugin named %s with UUID %s has been launched with the provided inputs", parameter.getPluginName(), uuid));
		
		return uuid.toString();
	}
	
	/**{@inheritDoc}*/
	@Override
	public boolean stop(String executionIdentifier) throws ExecutorException {
		return unSchedule(executionIdentifier, true, false);
	}
	
	/**{@inheritDoc}*/
	@Override
	public boolean unSchedule(String executionIdentifier) throws ExecutorException {
		return unSchedule(executionIdentifier, false, false);
	}
	
	/**{@inheritDoc}*/
	@Override
	public boolean unSchedule(String executionIdentifier, boolean globally)
			throws ExecutorException {
		return unSchedule(executionIdentifier, false, globally);
	}

	// TODO Manage better exception to to advise the caller
	protected boolean unSchedule(String executionIdentifier, boolean stopOnly, boolean globally) throws ExecutorException {
		boolean currentStopped = true;
		try {
			SmartExecutorScheduler smartExecutorScheduler = SmartExecutorScheduler.getInstance();
			UUID uuid  = UUID.fromString(executionIdentifier);
			smartExecutorScheduler.stop(uuid, stopOnly, globally);
		} catch (SchedulerNotFoundException snfe) {
			currentStopped = true;
			logger.error("Error unscheduling task {}", executionIdentifier, snfe);
		} catch(SchedulerException e){
			currentStopped = false;
			logger.error("Error unscheduling task {}", executionIdentifier, e);
		} catch(SchedulePersistenceException ex){
			currentStopped = true;
			logger.error("Error removing scheduled task from persistence.", ex);
		}
		return currentStopped;
	}
	
	/**{@inheritDoc}*/
	@Override
	public PluginState getState(String executionIdentifier) 
			throws PluginInstanceNotFoundException, ExecutorException {
		try {
			SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
			return persistenceConnector.getLastPluginInstanceState(UUID.fromString(executionIdentifier));
		} catch (Exception e) {
			throw new PluginInstanceNotFoundException();
		}
	}
	
	/**{@inheritDoc}*/
	@Override
	public PluginState getIterationState(String executionIdentifier, int iterationNumber) 
			throws PluginInstanceNotFoundException, ExecutorException {
		try {
			SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
			return persistenceConnector.getPluginInstanceState(UUID.fromString(executionIdentifier), iterationNumber);
		} catch (Exception e) {
			throw new PluginInstanceNotFoundException();
		}
	}
	
}
