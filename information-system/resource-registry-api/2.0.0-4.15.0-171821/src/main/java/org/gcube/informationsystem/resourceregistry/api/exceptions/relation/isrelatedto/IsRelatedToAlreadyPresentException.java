package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsRelatedToAlreadyPresentException extends RelationAlreadyPresentException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1851536496095114678L;

	public IsRelatedToAlreadyPresentException(String message) {
		super(message);
	}

	public IsRelatedToAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public IsRelatedToAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
