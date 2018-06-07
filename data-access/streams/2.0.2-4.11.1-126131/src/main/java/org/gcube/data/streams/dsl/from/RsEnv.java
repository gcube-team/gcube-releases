/**
 * 
 */
package org.gcube.data.streams.dsl.from;

import gr.uoa.di.madgik.grs.record.Record;

import java.net.URI;


/**
 * The environment in which {@code convert} sentences are evaluated.
 * 
 * @author Fabio Simeoni
 *
 */
class RsEnv<R extends Record> {
	
	protected URI locator;
	
	protected Class<R> recordClass;
	
	/**
	 * Creates a new instance from a resultset locator
	 * @param locator the locator
	 */
	public RsEnv(URI locator) {
		this.locator=locator;
	}
	
	/**
	 * Creates a new instance from a resultset locator
	 * @param locator the locator
	 * @param recordClass the class;
	 */
	public RsEnv(URI locator, Class<R> recordClass) {
		this.locator=locator;
		this.recordClass = recordClass;
	}
}
