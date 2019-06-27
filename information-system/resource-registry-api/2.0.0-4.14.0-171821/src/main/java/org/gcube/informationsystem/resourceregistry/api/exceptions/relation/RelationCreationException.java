package org.gcube.informationsystem.resourceregistry.api.exceptions.relation;

import org.gcube.informationsystem.resourceregistry.api.exceptions.CreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class RelationCreationException extends CreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 6390564714862731641L;
	
	public RelationCreationException(String message) {
		super(message);
	}

	public RelationCreationException(Throwable cause) {
		super(cause);
	}

	public RelationCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
