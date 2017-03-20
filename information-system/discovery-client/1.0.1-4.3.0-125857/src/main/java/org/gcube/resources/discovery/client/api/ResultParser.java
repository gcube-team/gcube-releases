package org.gcube.resources.discovery.client.api;

import org.gcube.resources.discovery.client.queries.api.Query;

/**
 * Transforms untyped results into typed results.
 * 
 * @author Fabio Simeoni
 *
 * @param <R> the result type
 * 
 * @see DiscoveryClient
 * @see Query
 */
public interface ResultParser<R> {

	/**
	 * Transforms an untyped result.
	 *  
	 * @param result the untyped results
	 * @return the typed result
	 * @throws Exception if the result cannot be typed
	 */
	R parse(String result) throws Exception;
}
