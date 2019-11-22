package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class EntityNotFoundException extends NotFoundException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1687373446724146351L;

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
