package org.gcube.informationsystem.publisher.stubs.registry.faults;

import javax.xml.ws.WebFault;

import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;

/**
 * Thrown by {@link RegistryStub#create(String, String)} when the resource type is invalid
 * service.
 */
@WebFault(name = "InvalidResourceFault")
public class InvalidResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with a given message and an {@link AxisFaultInfo} payload
	 * @param message the message
	 * @param info the payload
	 */
	public InvalidResourceException(String message) {
		super(message);
	}

	public InvalidResourceException(Exception e1) {
		super(e1);
	}
}

