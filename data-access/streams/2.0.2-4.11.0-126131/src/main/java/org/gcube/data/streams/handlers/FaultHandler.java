package org.gcube.data.streams.handlers;

import org.gcube.data.streams.Iteration;
import org.gcube.data.streams.Stream;

/**
 * Handlers of {@link Stream} iteration failures.
 * @author Fabio Simeoni
 *
 */
public interface FaultHandler {

	/** The ongoing iteration. */
	static final Iteration iteration = new Iteration();
	
	/**
	 * Indicates whether iteration should continue or stop the iteration on the occurrence of an iteration failure.
	 * @param failure the failure
	 * @throws RuntimeException if no element can be yielded from the input element
	 * 
	 */
	void handle(RuntimeException failure);
	
}
