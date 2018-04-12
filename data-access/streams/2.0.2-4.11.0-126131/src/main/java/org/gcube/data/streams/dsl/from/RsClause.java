package org.gcube.data.streams.dsl.from;

import gr.uoa.di.madgik.grs.record.Record;

/**
 * Partial clause implementation for {@code convert} sentences.
 * 
 * @author Fabio Simeoni
 *
 */
abstract class RsClause<R extends Record> {

	protected RsEnv<R> env;
	
	/**
	 * Creates an instance from a {@link RsEnv}
	 * @param env the environment
	 */
	public RsClause(RsEnv<R> env) {
		
		this.env=env;
	}
	
}
