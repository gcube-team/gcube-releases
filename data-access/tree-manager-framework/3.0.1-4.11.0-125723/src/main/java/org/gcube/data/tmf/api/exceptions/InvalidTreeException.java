/**
 * 
 */
package org.gcube.data.tmf.api.exceptions;

import org.gcube.data.streams.exceptions.StreamContingency;

/**
 * Raised when trees do not match expectations.
 * 
 * @author Fabio Simeoni
 *
 */
@StreamContingency
public class InvalidTreeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public InvalidTreeException() {
		super();
	}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public InvalidTreeException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public InvalidTreeException(String msg,Throwable cause) {super(msg,cause);}

	public InvalidTreeException(Throwable cause) {
		super(cause);
	}
}
