package org.gcube.informationsystem.exporter.mapper.exception;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

public class CreateException extends ResourceRegistryException {

	/**
	 *  Generated Serial Version UID
	 */
	private static final long serialVersionUID = 7815403701115692343L;

	public CreateException(String message) {
		super(message);
	}
	
	public CreateException(Throwable cause) {
		super(cause);
	}
	
	public CreateException(String message, Throwable cause) {
		super(message, cause);
	}
}
