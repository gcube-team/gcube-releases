package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContext;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class EntityAvailableInAnotherContextException extends ERAvailableInAnotherContextException implements AvailableInAnotherContext {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 6169522499840275744L;

	public EntityAvailableInAnotherContextException(String message) {
		super(message);
	}

	public EntityAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}

	public EntityAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}
}
