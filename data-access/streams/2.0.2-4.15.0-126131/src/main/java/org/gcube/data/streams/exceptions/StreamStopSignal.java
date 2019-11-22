package org.gcube.data.streams.exceptions;

import org.gcube.data.streams.Callback;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.handlers.FaultHandler;

/**
 * Used internally by {@link FaultHandler}s and {@link Callback}s to require the premature end of an iteration over a
 * {@link Stream}
 * 
 * @author Fabio Simeoni
 * 
 */
public class StreamStopSignal extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
