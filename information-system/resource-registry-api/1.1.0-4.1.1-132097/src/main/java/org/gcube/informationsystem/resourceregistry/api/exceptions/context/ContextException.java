package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ContextException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8119058749936021156L;

	public ContextException(String message) {
		super(message);
	}

	public ContextException(Throwable cause) {
		super(cause);
	}
	
	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
