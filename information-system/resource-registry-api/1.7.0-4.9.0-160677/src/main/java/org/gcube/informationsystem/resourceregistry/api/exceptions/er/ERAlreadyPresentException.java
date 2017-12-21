package org.gcube.informationsystem.resourceregistry.api.exceptions.er;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ERAlreadyPresentException extends ERException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1129094888903457750L;

	public ERAlreadyPresentException(String message) {
		super(message);
	}
	
	public ERAlreadyPresentException(Throwable cause) {
		super(cause);
	}
	
	public ERAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
