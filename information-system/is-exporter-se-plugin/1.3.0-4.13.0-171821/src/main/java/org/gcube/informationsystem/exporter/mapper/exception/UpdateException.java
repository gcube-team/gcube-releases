package org.gcube.informationsystem.exporter.mapper.exception;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

public class UpdateException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1473775004888760485L;

	
	public UpdateException(String message) {
		super(message);
	}
	
	public UpdateException(Throwable cause) {
		super(cause);
	}
	
	public UpdateException(String message, Throwable cause) {
		super(message, cause);
	}
}
