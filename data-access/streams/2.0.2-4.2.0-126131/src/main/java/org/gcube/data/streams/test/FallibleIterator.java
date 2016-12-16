package org.gcube.data.streams.test;

import java.util.Iterator;
import java.util.List;

import org.gcube.data.streams.exceptions.StreamSkipSignal;

/**
 * An {@link Iterator} that can be staged to throw faults as well as elements. Used for testing purposes.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream elements
 */
public class FallibleIterator<E> implements Iterator<E> {

	final List<? extends Object> elements;
	final Class<E> clazz;
	int index;

	/**
	 * Creates an instance with a given type of the elements and given actual elements.
	 * 
	 * @param clazz the type of elements
	 * @param elements the actual elements, including {@link RuntimeException}s
	 * @throws IllegalArgumentException if the elements are neither {@link RuntimeException}s nor have the declared type.
	 */
	public FallibleIterator(Class<E> clazz, List<? extends Object> elements) throws IllegalArgumentException {
		this.clazz = clazz;
		// upfront type checks
		for (Object e : elements) {
			try {
				if (!(e instanceof RuntimeException))
					clazz.cast(e);
			} catch (ClassCastException ex) {
				throw new IllegalArgumentException("invalid stream element: " + e + " is neither a " + clazz.getSimpleName()
						+ " nor a RuntimeException");
			}
		}

		this.elements = elements;
	}

	@Override
	public boolean hasNext() {
		if (index < elements.size()) {
			if (elements.get(index) instanceof StreamSkipSignal) {
				index++;
				return hasNext();
			} else
				return true;
		} else
			return false;

	}

	@Override
	public E next() {

		Object o = elements.get(index);

		index++;

		// throw unchecked as they are
		if (o instanceof RuntimeException)
			throw (RuntimeException) o;

		return clazz.cast(o);
	}

	@Override
	public void remove() {
		
	}

}
