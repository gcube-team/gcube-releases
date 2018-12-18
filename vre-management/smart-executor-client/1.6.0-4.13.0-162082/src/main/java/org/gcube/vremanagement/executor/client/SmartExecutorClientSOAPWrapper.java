package org.gcube.vremanagement.executor.client;

import java.util.UUID;

import org.gcube.vremanagement.executor.api.rest.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Added to launch plugins running on SOAP only old smart-executor
 */
@SuppressWarnings("deprecation")
class SmartExecutorClientSOAPWrapper implements SmartExecutor {
	
	public static final String PATH_SEPARATOR = "/";
	
	protected SmartExecutorProxy smartExecutorProxy;
	
	public SmartExecutorClientSOAPWrapper(SmartExecutorProxy smartExecutorProxy) {
		this.smartExecutorProxy = smartExecutorProxy;
	}

	public SmartExecutorClientSOAPWrapper(SmartExecutorPluginQuery query, ExecutorPlugin executorPlugin) {
		this.smartExecutorProxy = ExecutorPlugin.getExecutorProxy(query, executorPlugin).build();
	}

	@Override
	public String launch(String launchParameter)
			throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException {
		try {
			LaunchParameter lp = SEMapper.unmarshal(LaunchParameter.class, launchParameter);
			try {
				return smartExecutorProxy.launch(lp);
			}catch (Exception e) {
				throw (Exception) e.getCause();
			}
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new ExecutorException(e);
		}
	}
	
	@Override
	public UUID launch(LaunchParameter launchParameter)
			throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException {
		try {
			try {
				String uuid = smartExecutorProxy.launch(launchParameter);
				return UUID.fromString(uuid);
			}catch (Exception e) {
				throw (Exception) e.getCause();
			}
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getPluginStateEvolution(String executionIdentifier, Integer iterationNumber) throws ExecutorException {
		try {
			try {
				PluginStateEvolution pluginStateEvolution = null;
				if(iterationNumber!=null) {
					pluginStateEvolution = smartExecutorProxy.getIterationStateEvolution(executionIdentifier, iterationNumber);
				}else {
					pluginStateEvolution = smartExecutorProxy.getStateEvolution(executionIdentifier);
				}
				return SEMapper.marshal(pluginStateEvolution);
			}catch (Exception e) {
				throw (Exception) e.getCause();
			}
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new ExecutorException(e);
		}
	}
	
	@Override
	public PluginStateEvolution getPluginStateEvolution(UUID executionIdentifier, Integer iterationNumber) throws ExecutorException {
		try {
			try {
				PluginStateEvolution pluginStateEvolution = null;
				if(iterationNumber!=null) {
					pluginStateEvolution = smartExecutorProxy.getIterationStateEvolution(executionIdentifier.toString(), iterationNumber);
				}else {
					pluginStateEvolution = smartExecutorProxy.getStateEvolution(executionIdentifier.toString());
				}
				return pluginStateEvolution;
			}catch (Exception e) {
				throw (Exception) e.getCause();
			}
		} catch(ExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean delete(String executionIdentifier, boolean globally) throws ExecutorException {
		return smartExecutorProxy.unSchedule(executionIdentifier, globally);
	}
	
	@Override
	public boolean delete(UUID executionIdentifier, boolean globally) throws ExecutorException {
		return delete(executionIdentifier.toString(), globally);
	}
}
