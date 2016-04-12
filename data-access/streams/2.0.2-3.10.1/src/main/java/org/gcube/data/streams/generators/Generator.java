package org.gcube.data.streams.generators;

import org.gcube.data.streams.Iteration;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;

/**
 * Yields elements of a {@link Stream} from elements of another {@link Stream}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E1> the type of elements in the input stream
 * @param <E2> the type of elements in the output stream
 * 
 * @see Stream
 */
public interface Generator<E1, E2> {

	/** The ongoing iteration. */
	static final Iteration iteration = new Iteration();
	
	
	/**
	 * Yields an element of a {@link Stream} from an element of another {@link Stream}.
	 * 
	 * @param element the input element
	 * @return the output element
	 * @throws StreamSkipSignal if no element <em>should</em> be yielded from the input element (i.e. the element should
	 *             not contribute to the output stream)
	 * @throws StreamStopSignal if no further element should be yielded
	 * @throws RuntimeException if no element <em>can</em> be yielded from the input element
	 */
	E2 yield(E1 element) throws StreamSkipSignal,StreamStopSignal;
}
