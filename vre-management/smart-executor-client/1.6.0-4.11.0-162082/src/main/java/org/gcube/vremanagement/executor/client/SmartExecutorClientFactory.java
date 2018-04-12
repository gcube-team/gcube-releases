package org.gcube.vremanagement.executor.client;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gcube.vremanagement.executor.api.rest.SmartExecutor;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.query.Discover;
import org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter;
import org.gcube.vremanagement.executor.client.query.filter.ServiceEndpointQueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("deprecation")
public class SmartExecutorClientFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(SmartExecutorClientFactory.class);
	
	private static String FORCED_URL = null;
	
	protected static void forceToURL(String url) {
		FORCED_URL = url;
	}
	
	public static SmartExecutor create(String pluginName, Map<String,String> capabilites,
			ServiceEndpointQueryFilter serviceEndpointQueryFilter, GCoreEndpointQueryFilter gCoreEndpointQueryFilter) {
		
		if(FORCED_URL != null) {
			return new SmartExecutorClientImpl(pluginName, FORCED_URL);
		}
		
		Discover discover = new Discover(Constants.SERVICE_ENTRY_NAME);
		discover.filterByPluginName(pluginName);
		discover.filterByCapabilities(capabilites);
		discover.setServiceEndpointQueryFilter(serviceEndpointQueryFilter);
		discover.setGCoreEndpointQueryFilter(gCoreEndpointQueryFilter);
		List<String> addresses = discover.getAddresses();
		
		if(addresses == null || addresses.isEmpty()) {
			logger.debug("No REST smart-executor found. Looking for old SOAP Version");
			
			ExecutorPlugin executorPlugin = new ExecutorPlugin();
			discover = new SmartExecutorPluginQuery(executorPlugin);
			discover.filterByPluginName(pluginName);
			discover.filterByCapabilities(capabilites);
			discover.setServiceEndpointQueryFilter(serviceEndpointQueryFilter);
			discover.setGCoreEndpointQueryFilter(gCoreEndpointQueryFilter);
			
			addresses = discover.getAddresses();
			if(addresses == null || addresses.isEmpty()) {
				String error = String.format("No %s:%s found in the current context", Constants.SERVICE_CLASS,
						Constants.SERVICE_NAME);
				throw new RuntimeException(error);
			}
			return new SmartExecutorClientSOAPWrapper((SmartExecutorPluginQuery) discover, executorPlugin);
		}
		
		Random random = new Random();
		int index = random.nextInt(addresses.size());
		return new SmartExecutorClientImpl(pluginName, addresses.get(index));
	}
	
	public static SmartExecutor create(String pluginName) {
		return create(pluginName, null, null, null);
	}
	
}
