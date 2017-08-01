/**
 * 
 */
package org.gcube.data.tml.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;
import org.gcube.data.streams.exceptions.StreamContingency;
import org.gcube.data.tml.proxies.Path;
import org.gcube.data.tml.proxies.TReader;


/**
 * Raised by {@link TReader}s when {@link Path}s do not identify tree nodes.
 * 
 * @author Fabio Simeoni
 *
 */
@Unrecoverable
@StreamContingency
@WebFault(name="UnknownPathFault")
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
