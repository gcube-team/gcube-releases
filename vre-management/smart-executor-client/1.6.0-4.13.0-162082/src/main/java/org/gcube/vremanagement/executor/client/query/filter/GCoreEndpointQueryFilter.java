package org.gcube.vremanagement.executor.client.query.filter;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.EndpointDiscoveryFilter;

@SuppressWarnings("deprecation")
public interface GCoreEndpointQueryFilter extends EndpointDiscoveryFilter {
	
	@Override
	void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints);
	
}
