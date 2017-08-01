package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class EntityAlreadyPresentException extends ERAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1129094888903457750L;

	public EntityAlreadyPresentException(String message) {
		super(message);
	}
	
	public EntityAlreadyPresentException(Throwable cause) {
		super(cause);
	}
	
	public EntityAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
