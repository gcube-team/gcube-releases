package org.gcube.data.streams.test;

import org.gcube.data.streams.Stream;

/**
 * Generates a {@link Stream} for testing purposes.
 * 
 * @author Fabio Simeoni
 *
 */
public interface StreamProvider {

	/**
	 * Generates a {@link Stream}
	 * @return the stream.
	 */
	Stream<?> get();
}
