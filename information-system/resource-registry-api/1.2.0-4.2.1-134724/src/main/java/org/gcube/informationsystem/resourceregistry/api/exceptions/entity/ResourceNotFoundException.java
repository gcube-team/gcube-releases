package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ResourceNotFoundException extends ContextException implements ObjectNotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1687373446724146351L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
