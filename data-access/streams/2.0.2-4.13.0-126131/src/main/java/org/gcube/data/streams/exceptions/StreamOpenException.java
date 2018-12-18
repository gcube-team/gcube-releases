package org.gcube.data.streams.exceptions;

import org.gcube.data.streams.Stream;

/**
 * A failure that occurs when a {@link Stream} cannot be opened.
 *  
 * @author Fabio Simeoni
 *
 */
public class StreamOpenException extends StreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public StreamOpenException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an instance with a given message and a given cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public StreamOpenException(String msg, Throwable cause) {
		super(msg,cause);
	}

	
}
