/**
 * 
 */
package org.gcube.vremanagement.executor.client.query.filter;

import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@SuppressWarnings("deprecation")
public interface ServiceEndpointQueryFilter
		extends org.gcube.vremanagement.executor.client.plugins.query.filter.ServiceEndpointQueryFilter {
	
	@Override
	public void filter(SimpleQuery simpleQuery);
	
}
