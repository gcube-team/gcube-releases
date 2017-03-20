/**
 * 
 */
package org.gcube.data.tml.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;
import org.gcube.data.streams.exceptions.StreamContingency;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TWriter;


/**
 * Raised by {@link TReader}s and {@link TWriter}s when input identifiers do not identify trees.
 * 
 * @author Fabio Simeoni
 *
 */
@Unrecoverable
@StreamContingency
@WebFault(name="UnknownTreeFault")
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
