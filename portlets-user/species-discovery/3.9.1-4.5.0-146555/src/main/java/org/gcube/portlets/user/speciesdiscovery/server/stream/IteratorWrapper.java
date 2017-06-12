/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.util.Iterator;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class IteratorWrapper<E> implements CloseableIterator<E> {
	
	protected Iterator<E> iterator;

	/**
	 * @param iterator
	 */
	public IteratorWrapper(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E next() {
		return iterator.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
	}
}
