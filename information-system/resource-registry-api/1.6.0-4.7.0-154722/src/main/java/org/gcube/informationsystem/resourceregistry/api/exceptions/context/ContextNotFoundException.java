package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ContextNotFoundException extends ContextException implements NotFound {

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
