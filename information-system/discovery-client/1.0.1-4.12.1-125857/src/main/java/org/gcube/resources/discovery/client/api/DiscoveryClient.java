package org.gcube.resources.discovery.client.api;

import java.util.List;

import org.gcube.resources.discovery.client.queries.api.Query;

/**
 * Local interface for resource discovery.
 * <p>
 * Submits {@link Query}s for remote execution and returns a list of typed results.
 * 
 * @author Fabio Simeoni
 * 
 * @param <R> the type of query results
 * 
 */
public interface DiscoveryClient<R> {

	/**
	 * Submits a {@link Query} for remote execution and returns a list of typed results.
	 * 
	 * @param query the query
	 * @return the results
	 * @throws DiscoveryException if the query cannot be submitted
	 * @throws InvalidResultException if the results cannot be parsed. Implementations may adopt different degrees of
	 *             tolerance to parsing errors before raising this exception.
	 */
	List<R> submit(Query query) throws DiscoveryException, InvalidResultException;
}
