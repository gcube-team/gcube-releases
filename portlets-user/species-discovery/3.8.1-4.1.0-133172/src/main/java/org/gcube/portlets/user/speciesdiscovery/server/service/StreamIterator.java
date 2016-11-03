/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.service;

import org.gcube.data.streams.Stream;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class StreamIterator<T> implements CloseableIterator<T> {
	
	protected Stream<T> stream;

	/**
	 * @param stream
	 */
	public StreamIterator(Stream<T> stream) {
		this.stream = stream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return stream.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T next() {
		return stream.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		stream.remove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		stream.close();
	}

}
