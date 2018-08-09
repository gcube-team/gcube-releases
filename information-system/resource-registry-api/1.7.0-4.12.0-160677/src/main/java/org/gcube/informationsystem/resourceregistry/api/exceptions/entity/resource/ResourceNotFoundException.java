package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityNotFoundException;


/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ResourceNotFoundException extends EntityNotFoundException implements NotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8635077520624797114L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
