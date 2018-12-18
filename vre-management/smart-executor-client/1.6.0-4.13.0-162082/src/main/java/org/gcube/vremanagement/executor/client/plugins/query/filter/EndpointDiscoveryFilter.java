/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Use {@link GCoreEndpointQueryFilter} instead
 */
@Deprecated
public interface EndpointDiscoveryFilter {

	/**
	 * @param simpleQuery
	 * @param serviceEndpoints
	 */
	@Deprecated
	void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints);
	
}
