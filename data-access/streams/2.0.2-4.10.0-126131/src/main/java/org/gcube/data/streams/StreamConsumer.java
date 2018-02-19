package org.gcube.data.streams;

import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;

/**
 * A generic {@link Stream} consumer that delegates element processing and failure handling to a {@link Callback}.
 * @author Fabio Simeoni
 *
 * @param <T> the type of stream elements
 */
public final class StreamConsumer<T> {

	private final Stream<T> stream;
	private final Callback<T> callback;
	
	/**
	 * Creates an instance with a {@link Stream} and a {@link Callback}
	 * @param stream the stream
	 * @param callback the callback
	 */
	public StreamConsumer(Stream<T> stream, Callback<T> callback) {
		this.stream=stream;
		this.callback=callback;
	}
	
	/**
	 * Starts the iteration.
	 */
	public void start() {		
		consume();
	}
	
	//helper
	private void consume() {
		
		try {
			consuming: while (stream.hasNext()) {
			
				T next = stream.next();
				
				try {
					callback.consume(next);
				}
				catch(StreamSkipSignal skip) {
					continue consuming;
				}
				catch(StreamStopSignal stop) {
					break consuming;
				}
			}
		}
		finally {
			stream.close();
		}
		
	}
	

}


