package org.gcube.informationsystem.resourceregistry.api.exceptions.query;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class InvalidQueryException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5146082051190251441L;
	
	public InvalidQueryException(String message) {
		super(message);
	}
	
	public InvalidQueryException(Throwable cause) {
		super(cause);
	}
	
	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}	
	
}
