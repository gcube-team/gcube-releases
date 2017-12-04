package org.gcube.common.clients.queries;


/**
 * A callback to filter out {@link Query} results.
 * 
 * @author Fabio Simeoni
 * 
 * 
 * @param <R> the type of query results
 * 
 * @see Query
 *
 */
public interface ResultMatcher<R> {

	/**
	 * Returns <code>true</code> if the result should be retained.
	 * @param result the result
	 * @return <code>true</code> if the result should be retained
	 */
	boolean match(R result);
}
