package org.gcube.vremanagement.executor.rest;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.vremanagement.executor.ResourceInitializer;
import org.gcube.vremanagement.executor.api.rest.RestConstants;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.util.HTTPCall.HTTPMETHOD;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.InvalidInputsException;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.exception.SchedulerNotFoundException;
import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorScheduler;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorSchedulerFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

@Path(RestConstants.PLUGINS_PATH_PART)
public class RestSmartExecutor {
	
	private static Logger logger = LoggerFactory.getLogger(RestSmartExecutor.class);
	
	private static final String UUID_PATH_PARAM = "uuid";
	private static final String PLUGIN_NAME_PATH_PARAM = "pluginName";
	
	@POST
	@Path("/{" + PLUGIN_NAME_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(MediaType.TEXT_PLAIN)
	public String launch(@PathParam(PLUGIN_NAME_PATH_PARAM) String pluginName, String launchParameterString)
			throws ExecutorException {
		CalledMethodProvider.instance
				.set(HTTPMETHOD.POST.name() + " /" + RestConstants.PLUGINS_PATH_PART + "/" + PLUGIN_NAME_PATH_PARAM);
		
		try {
			logger.info("Requested to launch {} ({})", pluginName, launchParameterString);
			LaunchParameter launchParameter = SEMapper.unmarshal(LaunchParameter.class, launchParameterString);
			if(pluginName == null) {
				String error = String.format("Plugin Name provided in the URL (%s) cannot be null", pluginName);
				logger.error(error);
				throw new InputsNullException(error);
			}
			if(pluginName.compareTo(launchParameter.getPluginName()) != 0) {
				String error = String.format(
						"Plugin Name provided in the URL (%s) does not match with the one provided in %s (%s)",
						pluginName, LaunchParameter.class.getSimpleName(), launchParameter.getPluginName());
				logger.error(error);
				throw new InvalidInputsException(error);
			}
			
			SmartExecutorScheduler smartExecutorScheduler = SmartExecutorSchedulerFactory.getSmartExecutorScheduler();
			UUID uuid = smartExecutorScheduler.schedule(launchParameter, null);
			
			logger.info("{} ({}) has been lauched with uuid {}", pluginName, launchParameterString, uuid);
			
			return uuid.toString();
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new ExecutorException(e);
		}
	}
	
	@GET
	@Path("/{" + PLUGIN_NAME_PATH_PARAM + "}" + "/" + "{" + UUID_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String getPluginStateEvolution(@PathParam(PLUGIN_NAME_PATH_PARAM) String pluginName,
			@PathParam(UUID_PATH_PARAM) String executionIdentifier,
			@QueryParam(RestConstants.ITERATION_NUMBER_PARAM) Integer iterationNumber) throws ExecutorException {
		
		CalledMethodProvider.instance.set(HTTPMETHOD.GET.name() + " /" + RestConstants.PLUGINS_PATH_PART + "/"
				+ PLUGIN_NAME_PATH_PARAM + "/" + UUID_PATH_PARAM);
		
		PluginStateEvolution pluginStateEvolution = null;
		try {
			SmartExecutorPersistenceConnector persistenceConnector = SmartExecutorPersistenceFactory
					.getPersistenceConnector();
			pluginStateEvolution = persistenceConnector.getPluginInstanceState(UUID.fromString(executionIdentifier),
					iterationNumber);
			logger.info("{} for {} (iteration n. {}) is {}", PluginStateEvolution.class.getSimpleName(),
					executionIdentifier, iterationNumber, pluginStateEvolution);
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new ExecutorException(e);
		}
		
		if(pluginName.compareTo(pluginStateEvolution.getPluginDeclaration().getName()) != 0) {
			String error = String.format(
					"Plugin Name provided in the URL (%s) does not match with the one got from %s (%s)", pluginName,
					PluginStateEvolution.class.getSimpleName(), pluginStateEvolution.getPluginDeclaration().getName());
			throw new InvalidInputsException(error);
		}
		
		try {
			return SEMapper.marshal(pluginStateEvolution);
		} catch(JsonProcessingException e) {
			throw new ExecutorException(e);
		}
	}
	
	@DELETE
	@Path("/{" + PLUGIN_NAME_PATH_PARAM + "}" + "/" + "{" + UUID_PATH_PARAM + "}")
	public boolean delete(@PathParam(PLUGIN_NAME_PATH_PARAM) String pluginName,
			@PathParam(UUID_PATH_PARAM) String executionIdentifier,
			@QueryParam(RestConstants.GLOBALLY_PARAM) Boolean globally) throws ExecutorException {
		
		CalledMethodProvider.instance.set(HTTPMETHOD.DELETE.name() + " /" + RestConstants.PLUGINS_PATH_PART + "/"
				+ PLUGIN_NAME_PATH_PARAM + "/" + UUID_PATH_PARAM);
		
		try {
			if(globally == null) {
				globally = false;
			}
			
			logger.info("Requested to delete for {} with UUID {} {}", pluginName, executionIdentifier,
					globally ? RestConstants.GLOBALLY_PARAM : "");
			
			boolean currentStopped = true;
			try {
				SmartExecutorScheduler smartExecutorScheduler = SmartExecutorSchedulerFactory
						.getSmartExecutorScheduler();
				UUID uuid = UUID.fromString(executionIdentifier);
				smartExecutorScheduler.stop(uuid, globally);
			} catch(SchedulerNotFoundException e) {
				// currentStopped = true;
				logger.error("Error unscheduling task {}", executionIdentifier, e);
				throw new ExecutorException(e);
			} catch(SchedulerException e) {
				// currentStopped = false;
				logger.error("Error unscheduling task {}", executionIdentifier, e);
				throw new ExecutorException(e);
			} catch(SchedulePersistenceException e) {
				// currentStopped = true;
				logger.error("Error removing scheduled task from persistence.", e);
			} catch(ExecutorException e) {
				throw e;
			} catch(Exception e) {
				// currentStopped = false;
				logger.error("Error unscheduling task {}", executionIdentifier, e);
				throw new ExecutorException(e);
			}
			logger.info("{} with UUID {} was{} stopped successfully", pluginName, executionIdentifier,
					currentStopped ? "" : " NOT");
			return currentStopped;
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new ExecutorException(e);
		}
		
	}
	
	@GET
	@Path(RestConstants.SCHEDULED_PATH_PART)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String all(@QueryParam(RestConstants.GLOBALLY_PARAM) Boolean globally) throws ExecutorException {
		CalledMethodProvider.instance.set(HTTPMETHOD.GET.name() + " /" + RestConstants.SCHEDULED_PATH_PART);
		return "[]";
	}
	
}
