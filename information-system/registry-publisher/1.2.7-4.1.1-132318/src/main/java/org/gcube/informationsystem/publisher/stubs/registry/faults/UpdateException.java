package org.gcube.informationsystem.publisher.stubs.registry.faults;


import javax.xml.ws.WebFault;

import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;

/**
 * Thrown by {@link RegistryStub#update(String, String)} when something is failed on update 
 */
@WebFault(name = "UpdateFault")
public class UpdateException extends PublisherException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with a given message and an {@link AxisFaultInfo} payload
	 * @param message the message
	 * @param info the payload
	 */
	public UpdateException(String message) {
		super(message);
	}
}
