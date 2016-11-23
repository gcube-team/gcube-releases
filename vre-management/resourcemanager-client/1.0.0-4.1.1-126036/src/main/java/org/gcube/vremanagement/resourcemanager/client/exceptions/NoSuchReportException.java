package org.gcube.vremanagement.resourcemanager.client.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="NoSuchReportFaultType")
public class NoSuchReportException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an instance.
	 */
	public NoSuchReportException() {
		super();
	}
	
	/**
	 * Creates an instance with given message.
	 * @param msg the message
	 */
	public NoSuchReportException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an instance with a given message and cause.
	 * @param msg the message
	 * @param cause the cause
	 */
	public NoSuchReportException(String msg,Throwable cause) {super(msg,cause);}

	public NoSuchReportException(Throwable cause) {
		super(cause);
	}
}