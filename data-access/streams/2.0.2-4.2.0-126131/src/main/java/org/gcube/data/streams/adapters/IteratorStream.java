package org.gcube.data.streams.adapters;

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Stream} adapter for {@link Iterator}s.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of stream elements
 */
public class IteratorStream<E> implements Stream<E> {

	private static Logger log =LoggerFactory.getLogger(IteratorStream.class);	
	
	private final Iterator<E> iterator;
	private boolean closed;
	private final IteratorAdapter<E> adapter;
	
	
	/**
	 * Creates an instance that adapts a given {@link IteratorAdapter}.
	 * @param adapter the adapter
	 */
	public IteratorStream(IteratorAdapter<E> adapter) {
		this.iterator=adapter.iterator();
		this.adapter=adapter;
	}
	
	
	/**
	 * Creates an instance that adapts a given {@link Iterator} with a default {@link IteratorAdapter}.
	 * @param iterator the iterator
	 */
	public IteratorStream(final Iterator<E> iterator) {
		this(new IteratorAdapter<E>(iterator)); //use default adapter
	}
	
	@Override
	public boolean hasNext() {
		
		if (closed)
			return false; //respect close semantics
		
		else {
			
			boolean hasNext = iterator.hasNext();
			
			if (!hasNext)
				close();
			
			return hasNext;
		}
			
	}
	
	@Override
	public E next() {
		
		//respect close semantics
		if (closed)
			throw new NoSuchElementException();
		
		return iterator.next();
	}
	
	
	@Override
	public void close() {
		
		try {
			adapter.close();
		}
		catch(Exception e) {
			log.error("could not close iterator "+locator(),e);
		}
		finally {
			closed=true;
		}
	}
	
	@Override
	public URI locator() {
		return adapter.locator();
	}
	
	@Override
	public void remove() {
		iterator.remove();
	}
	
	@Override
	public boolean isClosed() {
		return closed;
	}
}
