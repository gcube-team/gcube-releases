package org.gcube.data.streams;

import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.handlers.FaultHandler;

/**
 * A model of a {@link Stream} iteration, with facilities to control it from within {@link Callback}s and {@link FaultHandler}s.
 * 
 * @author Fabio Simeoni
 *
 */
public final class Iteration {
	
	/**
	 * Stops the ongoing iteration.
	 */
	public void stop() throws StreamStopSignal {
		throw new StreamStopSignal();
	}

	/**
	 * Skip this element of the ongoing iteration.
	 */
	public void skip() throws StreamSkipSignal {
		throw new StreamSkipSignal();
	}
	
	
}
