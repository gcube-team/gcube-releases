package org.gcube.informationsystem.publisher.stubs.registry.faults;

import javax.xml.ws.WebFault;

import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;

/**
 * Thrown by {@link RegistryStub#remove(String, String)} when something is failed on remove 
 */
@WebFault(name = "RemoveFault")
public class RemoveException extends PublisherException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with a given message and an {@link AxisFaultInfo} payload
	 * @param message the message
	 * @param info the payload
	 */
	public RemoveException(String message) {
		super(message);
	}
}