package org.gcube.data.streams.exceptions;

import org.gcube.data.streams.Stream;

/**
 * A failure that occurs when first accessing a {@link Stream}.
 *  
 * @author Fabio Simeoni
 *
 */
public class StreamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public StreamException() {
		super();
	}
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public StreamException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an instance with a given message and a given cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public StreamException(String msg, Throwable cause) {
		super(msg,cause);
	}

	
}
