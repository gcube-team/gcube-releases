package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.CreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class EntityCreationException extends CreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4445129394716597536L;
	
	public EntityCreationException(String message) {
		super(message);
	}

	public EntityCreationException(Throwable cause) {
		super(cause);
	}

	public EntityCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
