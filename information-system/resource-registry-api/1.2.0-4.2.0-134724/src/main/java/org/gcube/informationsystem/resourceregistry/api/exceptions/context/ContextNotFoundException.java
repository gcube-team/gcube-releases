package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ContextNotFoundException extends ContextException implements ObjectNotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8119058749936021156L;

	public ContextNotFoundException(String message) {
		super(message);
	}


}
