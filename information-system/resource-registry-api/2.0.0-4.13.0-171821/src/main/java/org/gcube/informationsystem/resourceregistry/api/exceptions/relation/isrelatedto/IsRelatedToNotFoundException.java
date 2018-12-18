package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsRelatedToNotFoundException extends RelationNotFoundException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -6461249423856424696L;

	public IsRelatedToNotFoundException(String message) {
		super(message);
	}

	public IsRelatedToNotFoundException(Throwable cause) {
		super(cause);
	}

	public IsRelatedToNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
