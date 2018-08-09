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
import org.gcube.vremanagement.executor.client.SmartExecutorClientFactory;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.EndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.proxies.DefaultSmartExecutorProxy;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.client.util.Tuple;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Use {@link SmartExecutorClientFactory} instead
 */
@Deprecated
public class ExecutorPlugin extends AbstractPlugin<SmartExecutor,SmartExecutorProxy> {
	
	/**
	 * Use {@link SmartExecutorClientFactory#create(String, java.util.Map, org.gcube.vremanagement.executor.client.query.filter.ServiceEndpointQueryFilter, org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter)}
	 * instead
	 * @param pluginName
	 * @param tuples
	 * @param serviceEndpointQueryFilter
	 * @param endpointDiscoveryFilter
	 * @return
	 */
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy(String pluginName, Tuple<String,String>[] tuples,
			ServiceEndpointQueryFilter serviceEndpointQueryFilter, EndpointDiscoveryFilter endpointDiscoveryFilter) {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		query.addConditions(pluginName, tuples);
		query.setServiceEndpointQueryFilter(serviceEndpointQueryFilter);
		query.setEndpointDiscoveryFilter(endpointDiscoveryFilter);
		return new ProxyBuilderImpl<SmartExecutor,SmartExecutorProxy>(executorPlugin, query);
	}
	
	/**
	 * Use {@link SmartExecutorClientFactory#create(String, java.util.Map, org.gcube.vremanagement.executor.client.query.filter.ServiceEndpointQueryFilter, org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter)}
	 * instead
	 * @param pluginName
	 * @param tuples
	 * @return
	 */
	@SafeVarargs
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy(String pluginName, Tuple<String,String>... tuples) {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		query.addConditions(pluginName, tuples);
		return new ProxyBuilderImpl<SmartExecutor,SmartExecutorProxy>(executorPlugin, query);
	}
	
	/**
	 * Use {@link SmartExecutorClientFactory#create()} instead
	 * @return
	 */
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy() {
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		return new ProxyBuilderImpl<SmartExecutor,SmartExecutorProxy>(executorPlugin);
	}
	
	/**
	 * For internal use only to support backward compatibility 
	 * @param query
	 * @param executorPlugin
	 * @return
	 */
	@Deprecated
	public static ProxyBuilder<SmartExecutorProxy> getExecutorProxy(SmartExecutorPluginQuery query,
			ExecutorPlugin executorPlugin) {
		return new ProxyBuilderImpl<SmartExecutor,SmartExecutorProxy>(executorPlugin, query);
	}
	
	/**
	 * For internal use only to support backward compatibility
	 * 
	 */
	public ExecutorPlugin() {
		super(SmartExecutor.WEB_SERVICE_SERVICE_NAME);
	}
	
	/**
	 * For internal use only to support backward compatibility
	 * Use {@link SmartExecutorClientFactory} instead 
	 */
	@Override
	public Exception convert(Exception fault, ProxyConfig<?,?> proxyConfig) {
		return fault;
	}
	
	/**
	 * For internal use only to support backward compatibility 
	 * Use {@link SmartExecutorClientFactory#create(String)} instead
	 */
	@Override
	public SmartExecutorProxy newProxy(ProxyDelegate<SmartExecutor> proxyDelegate) {
		return new DefaultSmartExecutorProxy(proxyDelegate);
	}
	
	/**
	 * For internal use only to support backward compatibility 
	 * Use {@link SmartExecutorClientFactory} instead 
	 */
	@Override
	public SmartExecutor resolve(EndpointReference endpoint, ProxyConfig<?,?> proxyConfig) throws Exception {
		return StubFactory.stubFor(Constants.smartExecutor).at(endpoint);
	}
	
}
