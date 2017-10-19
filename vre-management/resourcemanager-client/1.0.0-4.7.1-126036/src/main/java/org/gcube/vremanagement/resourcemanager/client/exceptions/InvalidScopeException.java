package org.gcube.vremanagement.resourcemanager.client.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="InvalidScopeFaultType")
public class InvalidScopeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public InvalidScopeException() {
		super();
	}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public InvalidScopeException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public InvalidScopeException(String msg,Throwable cause) {super(msg,cause);}

	public InvalidScopeException(Throwable cause) {
		super(cause);
	}
}
