package org.gcube.data.streams.exceptions;

import org.gcube.data.streams.Stream;

/**
 * A failure that occurs when a {@link Stream} cannot be published.
 *  
 * @author Fabio Simeoni
 *
 */
public class StreamPublishException extends StreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public StreamPublishException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an instance with a given message and a given cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public StreamPublishException(String msg, Throwable cause) {
		super(msg,cause);
	}

	
}
