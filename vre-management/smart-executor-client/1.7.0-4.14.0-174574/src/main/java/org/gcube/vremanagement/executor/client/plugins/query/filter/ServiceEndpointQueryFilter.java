/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Use {@link org.gcube.vremanagement.executor.client.query.filter.ServiceEndpointQueryFilter} instead
 */
@Deprecated
public interface ServiceEndpointQueryFilter {

	public void filter(SimpleQuery simpleQuery);
	
}
