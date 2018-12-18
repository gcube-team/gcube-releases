package org.gcube.informationsystem.publisher.stubs.registry.faults;



import javax.xml.ws.WebFault;

import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;

/**
 * Thrown by {@link RegistryStub#create(String, String)} when the resource is not accepted cause it doesn't satisfy a requirement 
 */
@WebFault(name = "ResourceNotAcceptedFault")
public class ResourceNotAcceptedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with a given message and an {@link AxisFaultInfo} payload
	 * @param message the message
	 * @param info the payload
	 */
	public ResourceNotAcceptedException(String message) {
		super(message);
	}
}
