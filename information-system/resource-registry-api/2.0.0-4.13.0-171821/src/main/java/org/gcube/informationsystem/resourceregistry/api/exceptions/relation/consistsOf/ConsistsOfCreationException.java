package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationCreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ConsistsOfCreationException extends RelationCreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5968496386603935430L;
	
	public ConsistsOfCreationException(String message) {
		super(message);
	}

	public ConsistsOfCreationException(Throwable cause) {
		super(cause);
	}

	public ConsistsOfCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
