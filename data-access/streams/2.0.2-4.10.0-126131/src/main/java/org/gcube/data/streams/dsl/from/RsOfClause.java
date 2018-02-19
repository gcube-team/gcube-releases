package org.gcube.data.streams.dsl.from;

import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;

import java.net.URI;

import org.gcube.data.streams.publishers.RsStringRecordFactory;

/**
 * A {@link RsClause} in which the record type of the input resultset is configured.
 * 
 * @author Fabio Simeoni
 *
 */
public class RsOfClause<R extends Record> extends RsClause<R> {

	/**
	 * Creates an instance from a resultset.
	 * @param locator the locator of the resultset
	 */
	public RsOfClause(URI locator) {
		super(new RsEnv<R>(locator));
	}
	
	/**
	 * Configures the type of records in the input resultset
	 * @param clazz the record type
	 * @return the next clause in the sentence
	 */
	public <R2 extends R> RsWithClause<R2> of(Class<R2> clazz) {
		return new RsWithClause<R2>(new RsEnv<R2>(env.locator,clazz));
	}
	
	/**
	 * Configures the type of records in the input result set to {@link RsStringRecordFactory#STRING_RECORD}.
	 * @return the next clause in the sentence
	 */
	public RsStringWithClause ofStrings() {
		return new RsStringWithClause(new RsEnv<GenericRecord>(env.locator));
	}
	
}
