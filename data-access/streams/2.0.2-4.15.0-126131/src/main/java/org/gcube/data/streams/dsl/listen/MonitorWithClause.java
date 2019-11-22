package org.gcube.data.streams.dsl.listen;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.delegates.MonitoredStream;
import org.gcube.data.streams.delegates.StreamListener;
import org.gcube.data.streams.dsl.StreamClause;
import org.gcube.data.streams.dsl.StreamClauseEnv;

/**
 * The clause of {@code guard} sentences in which a {@link StreamListener} is configured on the stream.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream elements
 */
public class MonitorWithClause<E> extends StreamClause<E, StreamClauseEnv<E>> {

	/**
	 * Creates an instance from an input {@link Stream}
	 * 
	 * @param stream the stream
	 */
	public MonitorWithClause(Stream<E> stream) {
		super(new StreamClauseEnv<E>(stream));
	}

	/**
	 * Return a {@link Stream} configured with a given {@link StreamListener}.
	 * 
	 * @param listener the listener
	 * @return the stream
	 */
	public MonitoredStream<E> with(StreamListener listener) {
		return new MonitoredStream<E>(env.stream(), listener);
	}
}
