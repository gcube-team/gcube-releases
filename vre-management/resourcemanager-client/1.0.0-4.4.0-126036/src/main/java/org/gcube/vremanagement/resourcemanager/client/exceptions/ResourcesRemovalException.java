package org.gcube.vremanagement.resourcemanager.client.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="ResourcesRemovalFaultType")
public class ResourcesRemovalException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public ResourcesRemovalException() {
		super();
	}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public ResourcesRemovalException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public ResourcesRemovalException(String msg,Throwable cause) {super(msg,cause);}

	public ResourcesRemovalException(Throwable cause) {
		super(cause);
	}
}

