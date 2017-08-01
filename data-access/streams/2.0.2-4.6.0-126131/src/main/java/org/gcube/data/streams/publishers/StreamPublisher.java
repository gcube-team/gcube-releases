package org.gcube.data.streams.publishers;

import java.net.URI;

import org.gcube.data.streams.Stream;

/**
 * Publishes a {@link Stream} at a given address.
 * 
 * @author Fabio Simeoni
 *
 */
public interface StreamPublisher {

	/**
	 * Publishes the stream and returns its address.
	 * @return the address
	 * @throws StreamPublishException if the stream cannot be published
	 */
	URI publish();
}
