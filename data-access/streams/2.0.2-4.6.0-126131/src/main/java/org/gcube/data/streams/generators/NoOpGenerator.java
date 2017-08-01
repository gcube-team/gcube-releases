package org.gcube.data.streams.generators;


/**
 * A pass-through {@link Generator}.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of stream elements
 */
public class NoOpGenerator<E> implements Generator<E,E> {

	@Override
	public E yield(E element) {
		return element;
	};
}
