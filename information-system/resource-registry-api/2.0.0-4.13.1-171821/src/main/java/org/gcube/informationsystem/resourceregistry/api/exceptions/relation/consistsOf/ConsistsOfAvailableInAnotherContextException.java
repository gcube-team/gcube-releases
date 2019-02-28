package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ConsistsOfAvailableInAnotherContextException extends RelationAvailableInAnotherContextException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 3672357202181490781L;

	public ConsistsOfAvailableInAnotherContextException(String message) {
		super(message);
	}

	public ConsistsOfAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}

	public ConsistsOfAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
