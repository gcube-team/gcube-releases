package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContext;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceAvailableInAnotherContextException extends EntityAvailableInAnotherContextException implements AvailableInAnotherContext {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8722827977850574469L;

	public ResourceAvailableInAnotherContextException(String message) {
		super(message);
	}

	public ResourceAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}
	
	public ResourceAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
