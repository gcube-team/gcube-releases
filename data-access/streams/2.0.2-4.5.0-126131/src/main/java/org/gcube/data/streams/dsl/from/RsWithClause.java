package org.gcube.data.streams.dsl.from;

import gr.uoa.di.madgik.grs.record.Record;

import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.adapters.ResultsetStream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.generators.Generator;

/**
 * A {@link RsClause} in which the adapter of the input resultset is configured.
 * 
 * @author Fabio Simeoni
 * 
 * @param <R> the {@link Record} type of stream elements
 */
public class RsWithClause<R extends Record> extends RsClause<R> {

	/**
	 * Creates an instance with a {@link RsEnv}.
	 * 
	 * @param env the environment
	 */
	public RsWithClause(RsEnv<R> env) {
		super(env);
	}

	/**
	 * Returns a {@link Stream} with a given read timeout on the input resultset.
	 * 
	 * @param timeout the timeout
	 * @param unit the time unit of the timeout
	 * @return the stream
	 */
	public Stream<R> withTimeout(int timeout, TimeUnit unit) {
		ResultsetStream<Record> stream = new ResultsetStream<Record>(env.locator);
		stream.setTimeout(timeout, unit);
		return Streams.pipe(stream).through(new RecordDeserialiser<R>(env.recordClass));
		
	}

	/**
	 * Returns a {@link Stream} with a {@link ResultsetStream#default_timeout} in a
	 * {@link ResultsetStream#default_timeout_unit} on the input resultset.
	 * 
	 * @return the stream
	 */
	public Stream<R> withDefaults() {
		return new ResultsetStream<R>(env.locator);
	}
	
	// used internally to extract strings from records
	private static class RecordDeserialiser<R extends Record> implements Generator<Record,R> {
		
		private final Class<R> recordClass;
		
		public RecordDeserialiser(Class<R> recordClass) {
			this.recordClass=recordClass;
		}
		
		@Override
		public R yield(Record element) {
			return recordClass.cast(element);
		}
	}
}
