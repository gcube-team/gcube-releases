package org.gcube.data.streams.dsl.consume;

import org.gcube.data.streams.Callback;
import org.gcube.data.streams.StreamConsumer;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.StreamClause;
import org.gcube.data.streams.dsl.StreamClauseEnv;

/**
 * The clause of {@code consume} sentences in which a {@link Callback} is configured on the input stream.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream elements
 */
public class ConsumeWithClause<E> extends StreamClause<E, StreamClauseEnv<E>> {

	/**
	 * Creates an instance from an input {@link Stream}
	 * 
	 * @param stream the stream
	 */
	public ConsumeWithClause(Stream<E> stream) {
		super(new StreamClauseEnv<E>(stream));
	}

	/**
	 * Return a {@link Stream} configured with a given {@link Callback}.
	 * 
	 * @param consumer the consumer
	 */
	public <E2> void with(Callback<E> consumer) {
		new StreamConsumer<E>(env.stream(), consumer).start();
	}
}
