/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins;

import javax.xml.ws.EndpointReference;

import org.gcube.common.calls.jaxws.StubFactory;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.client.Constants;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.EndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.proxies.DefaultSmartExecutorProxy;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.client.util.Tuple;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ExecutorPlugin extends AbstractPlugin<SmartExecutor, SmartExecutorProxy> {
	
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy(
			String pluginName, 
			Tuple<String, String>[] tuples, 
			ServiceEndpointQueryFilter serviceEndpointQueryFilter,
			EndpointDiscoveryFilter endpointDiscoveryFilter) {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		query.addConditions(pluginName, tuples);
		query.setServiceEndpointQueryFilter(serviceEndpointQueryFilter);
		query.setEndpointDiscoveryFilter(endpointDiscoveryFilter);
		return new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(executorPlugin, query);
	}
	
	@SafeVarargs
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy(String pluginName, Tuple<String, String> ... tuples) {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		query.addConditions(pluginName, tuples);
		return new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(executorPlugin, query);
	}
	
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy() {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		return new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(executorPlugin);
	}
	
	public ExecutorPlugin(){
		super(SmartExecutor.WEB_SERVICE_SERVICE_NAME);
	}
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxyConfig) {
		return fault;
	}

	@Override
	public SmartExecutorProxy newProxy(ProxyDelegate<SmartExecutor> proxyDelegate) {
		return new DefaultSmartExecutorProxy(proxyDelegate);
	}

	@Override
	public SmartExecutor resolve(EndpointReference endpoint, ProxyConfig<?, ?> proxyConfig)
			throws Exception {
		return StubFactory.stubFor(Constants.smartExecutor).at(endpoint);
	}

}
