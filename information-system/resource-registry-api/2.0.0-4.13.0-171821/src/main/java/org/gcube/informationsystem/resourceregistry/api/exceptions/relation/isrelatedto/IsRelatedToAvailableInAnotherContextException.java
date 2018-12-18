package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsRelatedToAvailableInAnotherContextException extends RelationAvailableInAnotherContextException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 8320730396137844754L;

	public IsRelatedToAvailableInAnotherContextException(String message) {
		super(message);
	}

	public IsRelatedToAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}

	public IsRelatedToAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
