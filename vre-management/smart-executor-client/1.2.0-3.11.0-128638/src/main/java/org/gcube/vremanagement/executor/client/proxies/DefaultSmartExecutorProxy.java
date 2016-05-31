/**
 * 
 */
package org.gcube.vremanagement.executor.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class DefaultSmartExecutorProxy implements SmartExecutorProxy {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(DefaultSmartExecutorProxy.class);
	
	private ProxyDelegate<SmartExecutor> proxyDelegate;
	
	public DefaultSmartExecutorProxy(ProxyDelegate<SmartExecutor> proxyDelegate) {
		this.proxyDelegate = proxyDelegate;
	}

	/** {@inheritDoc} */
	@Override
	public String launch(final LaunchParameter launchParameter)
			throws InputsNullException, PluginNotFoundException,
			LaunchException, ExecutorException {
		
		Call<SmartExecutor, String> call = new Call<SmartExecutor, String>() {
			@Override
			public String call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling launch() function");
				return endpoint.launch(launchParameter);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call launch() function");
			throw again(e).asServiceException();
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean unSchedule(final String executionIdentifier) throws ExecutorException {
		Call<SmartExecutor, Boolean> call = new Call<SmartExecutor, Boolean>() {
			@Override
			public Boolean call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling unSchedule(String) function");
				return endpoint.unSchedule(executionIdentifier);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call unSchedule(String) function");
			throw again(e).asServiceException();
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean unSchedule(final String executionIdentifier, final boolean globally) throws ExecutorException {
		Call<SmartExecutor, Boolean> call = new Call<SmartExecutor, Boolean>() {
			@Override
			public Boolean call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling unSchedule(String,boolean) function");
				return endpoint.unSchedule(executionIdentifier, globally);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call unSchedule(String,boolean) function");
			throw again(e).asServiceException();
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public PluginState getState(final String executionIdentifier)
			throws PluginInstanceNotFoundException, ExecutorException {
		
		Call<SmartExecutor, PluginState> call = new Call<SmartExecutor, PluginState>() {
			@Override
			public PluginState call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling getState(String) function");
				return endpoint.getState(executionIdentifier);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call getState(String) function");
			throw again(e).asServiceException();
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public PluginState getIterationState(final String executionIdentifier, final int iterationNumber)
			throws PluginInstanceNotFoundException, ExecutorException {
		
		Call<SmartExecutor, PluginState> call = new Call<SmartExecutor, PluginState>() {
			@Override
			public PluginState call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling getIterationState(String, int) function");
				return endpoint.getIterationState(executionIdentifier, iterationNumber);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call getIterationState(String, int) function");
			throw again(e).asServiceException();
		}
	}


	@Override
	public boolean stop(final String executionIdentifier) throws ExecutorException {
		
		Call<SmartExecutor, Boolean> call = new Call<SmartExecutor, Boolean>() {
			@Override
			public Boolean call(SmartExecutor endpoint) throws Exception {
				logger.debug("Calling stop(String) function");
				return endpoint.stop(executionIdentifier);
			}
		};
		
		try {
			return proxyDelegate.make(call);
		} catch (Exception e) {
			logger.debug("Failed to call stop(String) function");
			throw again(e).asServiceException();
		}
	}
	
	

}
