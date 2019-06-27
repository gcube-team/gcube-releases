package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ContextNotFoundException extends NotFoundException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 3034336911176161784L;

	public ContextNotFoundException(String message) {
		super(message);
	}

	public ContextNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ContextNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
