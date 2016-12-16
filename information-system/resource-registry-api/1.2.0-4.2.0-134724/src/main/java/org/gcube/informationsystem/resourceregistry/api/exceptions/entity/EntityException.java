package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class EntityException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8119058749936021156L;

	public EntityException(String message) {
		super(message);
	}

	public EntityException(Throwable cause) {
		super(cause);
	}
	
	public EntityException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
