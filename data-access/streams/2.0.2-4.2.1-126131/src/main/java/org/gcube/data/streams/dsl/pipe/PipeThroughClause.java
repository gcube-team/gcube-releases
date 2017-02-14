package org.gcube.data.streams.dsl.pipe;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.delegates.PipedStream;
import org.gcube.data.streams.dsl.StreamClause;
import org.gcube.data.streams.dsl.StreamClauseEnv;
import org.gcube.data.streams.generators.Generator;

/**
 * The clause of {@code pipe} sentences in which a {@link Generator} is configured on the output stream.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream elements
 */
public class PipeThroughClause<E> extends StreamClause<E, StreamClauseEnv<E>> {

	/**
	 * Creates an instance from an input {@link Stream}
	 * 
	 * @param stream the stream
	 */
	public PipeThroughClause(Stream<E> stream) {
		super(new StreamClauseEnv<E>(stream));
	}

	/**
	 * Return a {@link Stream} configured with a given {@link Generator}.
	 * 
	 * @param generator the generator
	 * @return the stream
	 */
	public <E2> PipedStream<E, E2> through(Generator<E, E2> generator) {
		return new PipedStream<E, E2>(env.stream(), generator);
	}
}
