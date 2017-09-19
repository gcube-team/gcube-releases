package org.gcube.vremanagement.softwaregateway.client.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="ServiceNotAvaiableFault")
public class ServiceNotAvaiableException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public ServiceNotAvaiableException() {
		super();
	}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public ServiceNotAvaiableException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public ServiceNotAvaiableException(String msg,Throwable cause) {super(msg,cause);}

	public ServiceNotAvaiableException(Throwable cause) {
		super(cause);
	}
}
