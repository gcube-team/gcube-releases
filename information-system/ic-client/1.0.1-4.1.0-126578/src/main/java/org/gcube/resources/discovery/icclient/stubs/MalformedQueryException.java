package org.gcube.resources.discovery.icclient.stubs;

import javax.xml.ws.WebFault;

/**
 * Thrown by {@link CollectorStub#execute(String)} when the query cannot be submitted to the Information Collector
 * service.
 */
@WebFault(name = "XQueryFaultType")
public class MalformedQueryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with a given message and an {@link AxisFaultInfo} payload
	 * @param message the message
	 * @param info the payload
	 */
	public MalformedQueryException(String message) {
		super(message);
	}
}
