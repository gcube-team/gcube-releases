package org.gcube.data.streams.generators;

import org.gcube.data.streams.exceptions.StreamSkipSignal;

/**
 * A partial implementation of {@link Filter} that provides support for skipping elements
 * @author Fabio Simeoni
 *
 * @param <E1> the type of input elements
 * @param <E2> the type of yielded elements
 */
public abstract class Filter<E1,E2> implements Generator<E1,E2>  {

	private final StreamSkipSignal skip = new StreamSkipSignal();
	
	/**
	 * Invoked to skip the current element.
	 */
	protected void skip() {
		throw skip;
	}
}
