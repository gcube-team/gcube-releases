/**
 * 
 */
package org.gcube.data.streams.dsl.publish;

import gr.uoa.di.madgik.grs.record.Record;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.StreamClause;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.publishers.RecordFactory;
import org.gcube.data.streams.publishers.RsStringRecordFactory;

/**
 * The clause of {@code publish} sentences in which the type of {@link Record}s is configured on the output resultset.
 * 
 * @author Fabio Simeoni
 *
 */
public class PublishRsUsingClause<E> extends StreamClause<E,PublishRsEnv<E>> {
	
	/**
	 * Creates an instance with an input {@link Stream}.
	 * @param stream the stream
	 */
	public PublishRsUsingClause(Stream<E> stream) {
		super(new PublishRsEnv<E>(stream));
	}
	
	/**
	 * Configures a serialiser for the elements of the input {@link Stream}.
	 * @param serialiser the serialiser
	 * @return the next clause in the sentence
	 */
	public PublishRsWithClause<E> using(Generator<E,String> serialiser) {
		env.factory = new RsStringRecordFactory<E>(serialiser);
		return new PublishRsWithClause<E>(env);
	}
	
	/**
	 * Configures a {@link RecordFactory} for the elements of the input {@link Stream}.
	 * @param factory the factory
	 * @return the next clause in the sentence
	 */
	public PublishRsWithClause<E> using(RecordFactory<E> factory) {
		env.factory = factory;
		return new PublishRsWithClause<E>(env);
	}
}