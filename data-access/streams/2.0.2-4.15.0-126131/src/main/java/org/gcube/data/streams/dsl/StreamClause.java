/**
 * 
 */
package org.gcube.data.streams.dsl;

import org.gcube.data.streams.Stream;



/**
 * 
 * Base implementation for clauses of {@link Stream} sentences.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of elements of the stream
 * @param <ENV> the type of environment in which the clause is evaluated
 */
public class StreamClause<E,ENV extends StreamClauseEnv<E>> {
	
	protected ENV env;
	
	/**
	 * Creates an instance with a given evaluation environment.
	 * @param e the environment
	 */
	public StreamClause(ENV e) {
		env=e;
	}
}