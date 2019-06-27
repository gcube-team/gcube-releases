package org.gcube.vremanagement.executor.client;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.vremanagement.executor.api.rest.RestConstants;
import org.gcube.vremanagement.executor.api.rest.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.util.HTTPCall;
import org.gcube.vremanagement.executor.client.util.HTTPCall.HTTPMETHOD;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SmartExecutorClientImpl implements SmartExecutor {
	
	private static final Logger logger = LoggerFactory.getLogger(SmartExecutorClientImpl.class);
	
	public static final String PATH_SEPARATOR = "/";
	
	protected final String pluginName;
	protected final String address;
	protected HTTPCall httpCall;
	
	public SmartExecutorClientImpl(String pluginName, String address) {
		this.pluginName = pluginName;
		this.address = address;
		
	}
	
	private HTTPCall getHTTPCall() throws MalformedURLException {
		if(httpCall == null) {
			httpCall = new HTTPCall(address, SmartExecutorClientImpl.class.getSimpleName());
		}
		return httpCall;
	}

	@Override
	public String launch(String launchParameter)
			throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException {
		try {
			
			logger.info("Going to launch {} ", launchParameter);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(RestConstants.PLUGINS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(pluginName);
			
			HTTPCall httpCall = getHTTPCall();
			String uuid = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.POST, launchParameter);
			
			logger.debug("{} launched with UUID {} ", pluginName, uuid);
			return uuid;
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public UUID launch(LaunchParameter launchParameter)
			throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException {
		try {
			String uuid = launch(SEMapper.marshal(launchParameter));
			return UUID.fromString(uuid);
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public String getPluginStateEvolution(String executionIdentifier, Integer iterationNumber) throws PluginInstanceNotFoundException, ExecutorException {
		try {
			logger.info("Going to get {} of {} with UUID {} ", PluginStateEvolution.class.getSimpleName(), pluginName, executionIdentifier);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(RestConstants.PLUGINS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(pluginName);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(executionIdentifier);
			
			HTTPCall httpCall = getHTTPCall();
			Map<String, Object> parameters = new HashMap<>();
			if(iterationNumber!=null) {
				parameters.put(RestConstants.ITERATION_NUMBER_PARAM, iterationNumber.intValue());
			}
			
			String pluginStateEvolution = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			return pluginStateEvolution;
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public PluginStateEvolution getPluginStateEvolution(UUID executionIdentifier, Integer iterationNumber) throws PluginInstanceNotFoundException, ExecutorException {
		try {
			String pluginStateEvolution = getPluginStateEvolution(executionIdentifier.toString(), iterationNumber);
			return SEMapper.unmarshal(PluginStateEvolution.class, pluginStateEvolution);
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean delete(String executionIdentifier, boolean globally) throws ExecutorException {
		try {
			logger.info("Going to stop plugin with UUID {} ", executionIdentifier);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(RestConstants.PLUGINS_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(pluginName);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(executionIdentifier);
			
			HTTPCall httpCall = getHTTPCall();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(RestConstants.GLOBALLY_PARAM, globally);
			Boolean stopped = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE, parameters);
			
			logger.debug("Plugin with UUID {} {} stopped", executionIdentifier, stopped ? "successfully" : "was not");
			return stopped;
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean delete(UUID executionIdentifier, boolean globally) throws ExecutorException {
		return delete(executionIdentifier.toString(), globally);
	}

}
