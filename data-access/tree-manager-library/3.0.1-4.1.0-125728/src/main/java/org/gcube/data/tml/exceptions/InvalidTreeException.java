/**
 * 
 */
package org.gcube.data.tml.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;
import org.gcube.data.streams.exceptions.StreamContingency;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.trees.data.Tree;

/**
 * Raised by {@link TReader}s and {@link TWriter}s when input {@link Tree}s do not match expectations.
 * 
 * @author Fabio Simeoni
 * @see TReader
 * @see TWriter
 *
 */
@Unrecoverable
@StreamContingency
@WebFault(name="InvalidTreeFault")
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
