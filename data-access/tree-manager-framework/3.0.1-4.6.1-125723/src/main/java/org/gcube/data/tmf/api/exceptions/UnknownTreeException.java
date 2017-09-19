/**
 * 
 */
package org.gcube.data.tmf.api.exceptions;

import org.gcube.data.streams.exceptions.StreamContingency;


/**
 * Raised when identifiers do not identify trees.
 * 
 * @author Fabio Simeoni
 *
 */
@StreamContingency
public class UnknownTreeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public UnknownTreeException(){}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public UnknownTreeException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public UnknownTreeException(String msg,Throwable cause) {
		super(msg,cause);
	}
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public UnknownTreeException(Throwable cause) {
		super(cause);
	}
}
