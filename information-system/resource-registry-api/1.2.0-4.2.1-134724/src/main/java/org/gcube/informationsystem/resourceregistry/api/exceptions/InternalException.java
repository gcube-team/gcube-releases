package org.gcube.informationsystem.resourceregistry.api.exceptions;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class InternalException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1785473902200354712L;


	public InternalException(String message) {
		super(message);
	}

	public InternalException(Throwable cause) {
		super(cause);
	}
	
	public InternalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
