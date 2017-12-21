/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ListEndpointDiscoveryFilter implements EndpointDiscoveryFilter {

	@Override
	public void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints) {
		
		String expression = "";
		int size = serviceEndpoints.size();
		for(int i=0; i<size; i++){
			String hostedOn = serviceEndpoints.get(i).profile().runtime().hostedOn();
			String condition = String.format(SmartExecutorPluginQuery.containsFormat, hostedOn);
			expression = String.format("%s %s", expression, condition);
			if(i<(size-1)){
				expression = String.format("%s or ", expression);
			}
		}
		
		simpleQuery.addCondition(expression);
		
	}

}
