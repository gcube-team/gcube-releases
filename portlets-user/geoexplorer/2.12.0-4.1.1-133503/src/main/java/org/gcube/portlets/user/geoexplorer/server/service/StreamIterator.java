package org.gcube.portlets.user.geoexplorer.server.service;

import java.util.Iterator;

import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;

/**
 * 
 * @author francesco
 *
 * @param <T>
 */
public class StreamIterator<T> implements CloseableIterator<T> {
	
	protected Iterator<T> stream;

	/**
	 * @param stream
	 */
	public StreamIterator(Iterator<T> stream) {
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
//		stream.close();
	}

}

