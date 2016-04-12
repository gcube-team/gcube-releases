/**
 * 
 */
package org.gcube.vremanagement.executor.client;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.dataanalysis.executor.plugin.GenericWorkerPluginDeclaration;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ListEndpointDiscoveryFilter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SmartGenericWorkerDiscoveryQuery {
	
	private static Logger logger = LoggerFactory.getLogger(SmartGenericWorkerDiscoveryQuery.class);
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGenericWorkerDiscoveryQuery() throws Exception {
		
		//GenericWorkerPluginDeclaration gwpd = new GenericWorkerPluginDeclaration();
		
		ScopeProvider.instance.set("/gcube");
		 
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
 
		/*
		 add key_value filter here
		 * Tuple<String, String>[] tuples = new Tuple[n];
		 * 
		 * runQuery.addConditions(pluginName, tuples);
		*/
 
		//query.addConditions(gwpd.getName());
 
		/* Used to add extra filter to ServiceEndpoint discovery */
		query.setServiceEndpointQueryFilter(null);
		List<String> nodes = query.discoverEndpoints(new ListEndpointDiscoveryFilter());
		logger.debug("Found the following nodes: "+nodes+" in scope "+ScopeProvider.instance.get());
		
	}
	
}
