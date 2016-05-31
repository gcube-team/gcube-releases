package org.gcube.data.streams.exceptions;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.handlers.FaultHandler;

/**
 * Used in {@link Generator}s or {@link FaultHandler}s  to signals that the current element of a {@link Stream} should be skipped.
 *  
 * @author Fabio Simeoni
 *
 */
public class StreamSkipSignal extends StreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public StreamSkipSignal() {
		super();
	}

	
}
