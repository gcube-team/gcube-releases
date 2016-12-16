package org.gcube.common.clients.gcore.queries;

import java.util.List;
import java.util.Map;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.core.informationsystem.client.ISTemplateQuery;

/**
 * Executes {@link ISTemplateQuery}s.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ISFacade {

	/**
	 * Executes an query with a set of custom condition.
	 * @param queryClass the query class
	 * @param conditions the conditions
	 * @return the list of results
	 * @throws DiscoveryException if the query execution failed
	 * 
	 */
	public <R,Q extends ISTemplateQuery<R>> List<R> execute (Class<Q> queryClass, Map<String,String> conditions) throws DiscoveryException;
}
