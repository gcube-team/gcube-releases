/**
 * 
 */
package org.gcube.data.streams.dsl.publish;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.StreamClauseEnv;
import org.gcube.data.streams.publishers.RecordFactory;


/**
 * The {@link StreamClauseEnv} in which {@code publish} sentences are evaluated.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of elements of the input stream
 *
 */
public class PublishRsEnv<E> extends StreamClauseEnv<E> {
	
	RecordFactory<E> factory;
	
	/**
	 * Creates an instance with a {@link Stream}
	 * @param stream the stream
	 */
	public PublishRsEnv(Stream<E> stream) {
		super(stream);
	}
	
	/**
	 * Creates an instance with a {@link Stream} and a {@link RecordFactory}
	 * @param stream the stream
	 * @param factory the factory
	 */
	public PublishRsEnv(Stream<E> stream, RecordFactory<E> factory) {
		super(stream);
		this.factory=factory;
	}
}
