/**
 * 
 */
package org.gcube.data.tmf.api.exceptions;

import org.gcube.data.streams.exceptions.StreamContingency;


/**
 * Raised when paths fail to identify nodes in a given context.
 * @author Fabio Simeoni
 *
 */
@StreamContingency
public class UnknownPathException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public UnknownPathException(){}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public UnknownPathException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public UnknownPathException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	/**
	 * Creates an instance with a given cause.
	 * @param cause the cause
	 */
	public UnknownPathException(Throwable cause) {
		super(cause);
	}
}
