package org.gcube.informationsystem.resourceregistry.api.exceptions.relation;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class RelationAvailableInAnotherContextException extends AvailableInAnotherContextException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8555707367745327973L;

	public RelationAvailableInAnotherContextException(String message) {
		super(message);
	}

	public RelationAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}

	public RelationAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
