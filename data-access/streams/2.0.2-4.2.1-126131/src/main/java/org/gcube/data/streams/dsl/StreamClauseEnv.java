/**
 * 
 */
package org.gcube.data.streams.dsl;

import org.gcube.data.streams.Stream;


/**
 * The environment in which a {@link Stream} sentence is evaluated.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of elements of the input stream
 *
 */
public class StreamClauseEnv<E> {
	
	private final Stream<E> stream;
	
	public StreamClauseEnv(Stream<E> stream) {
		this.stream=stream;
	}
	
	public Stream<E> stream() {
		return stream;
	}
}
