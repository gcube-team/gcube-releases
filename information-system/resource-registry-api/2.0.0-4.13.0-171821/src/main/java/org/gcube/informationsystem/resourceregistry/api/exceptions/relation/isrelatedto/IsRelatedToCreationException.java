package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto;

import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationCreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class IsRelatedToCreationException extends RelationCreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -310747999683731578L;

	public IsRelatedToCreationException(String message) {
		super(message);
	}

	public IsRelatedToCreationException(Throwable cause) {
		super(cause);
	}

	public IsRelatedToCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
