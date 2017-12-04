package org.gcube.informationsystem.resourceregistry.api.exceptions.er;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContext;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ERAvailableInAnotherContextException extends ERException implements AvailableInAnotherContext {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7499131763751652582L;

	public ERAvailableInAnotherContextException(String message) {
		super(message);
	}
	
	public ERAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}
	
	public ERAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
