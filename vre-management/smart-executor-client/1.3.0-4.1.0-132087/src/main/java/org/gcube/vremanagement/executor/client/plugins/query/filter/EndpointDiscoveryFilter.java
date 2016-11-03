/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface EndpointDiscoveryFilter {

	/**
	 * @param simpleQuery
	 * @param serviceEndpoints
	 */
	void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints);
	
}
