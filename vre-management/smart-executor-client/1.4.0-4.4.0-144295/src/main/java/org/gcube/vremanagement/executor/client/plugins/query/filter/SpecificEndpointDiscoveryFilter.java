/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import static java.lang.String.format;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SpecificEndpointDiscoveryFilter implements EndpointDiscoveryFilter {

	private String endpointURI;
	
	public SpecificEndpointDiscoveryFilter(String endpointURI){
		this.endpointURI = endpointURI;
	}
	
	@Override
	public void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints) {
		simpleQuery.addCondition(format(SmartExecutorPluginQuery.containsFormat, endpointURI));
	}

}
