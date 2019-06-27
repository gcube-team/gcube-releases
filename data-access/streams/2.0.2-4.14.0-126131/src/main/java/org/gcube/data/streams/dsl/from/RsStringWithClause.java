package org.gcube.data.streams.dsl.from;

import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

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
 */
public class RsStringWithClause extends RsClause<GenericRecord> {

	// used internally to extract strings from records
	private static Generator<GenericRecord, String> recordSerialiser = new Generator<GenericRecord, String>() {
		@Override
		public String yield(GenericRecord element) {
			return ((StringField) element.getField(0)).getPayload();
		}
	};

	/**
	 * Creates an instance with a {@link RsEnv}.
	 * 
	 * @param env the environment
	 */
	public RsStringWithClause(RsEnv<GenericRecord> env) {
		super(env);
	}

	/**
	 * Returns a {@link Stream} with a given read timeout on the input resultset
	 * 
	 * @param timeout the timeout
	 * @param unit the time unit of the timeout
	 * @return the stream
	 */
	public Stream<String> withTimeout(int timeout, TimeUnit unit) {
		ResultsetStream<GenericRecord> recordStream = new ResultsetStream<GenericRecord>(env.locator);
		recordStream.setTimeout(timeout, unit);
		return Streams.pipe(recordStream).through(recordSerialiser);
	}

	/**
	 * Returns a {@link Stream} with a {@link ResultsetStream#default_timeout} in a
	 * {@link ResultsetStream#default_timeout_unit} on the input resultset.
	 * 
	 * @return the stream
	 */
	public Stream<String> withDefaults() {
		ResultsetStream<GenericRecord> recordStream = new ResultsetStream<GenericRecord>(env.locator);
		return Streams.pipe(recordStream).through(recordSerialiser);
	}
}
