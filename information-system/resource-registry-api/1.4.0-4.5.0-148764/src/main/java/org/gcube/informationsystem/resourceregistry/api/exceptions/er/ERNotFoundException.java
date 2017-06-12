package org.gcube.informationsystem.resourceregistry.api.exceptions.er;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ERNotFoundException extends ERException implements NotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1687373446724146351L;

	public ERNotFoundException(String message) {
		super(message);
	}

	public ERNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ERNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
