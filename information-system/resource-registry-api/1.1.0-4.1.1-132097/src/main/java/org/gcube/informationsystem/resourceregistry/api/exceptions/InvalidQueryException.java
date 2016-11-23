package org.gcube.informationsystem.resourceregistry.api.exceptions;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class InvalidQueryException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5146082051190251441L;

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQueryException(String message) {
		super(message);
	}
			
	
}
