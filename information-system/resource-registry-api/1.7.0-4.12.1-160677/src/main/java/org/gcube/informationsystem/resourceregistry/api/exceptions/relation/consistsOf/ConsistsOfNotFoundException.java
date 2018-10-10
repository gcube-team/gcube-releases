package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ConsistsOfNotFoundException extends RelationNotFoundException implements NotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8268203168632780089L;

	public ConsistsOfNotFoundException(String message) {
		super(message);
	}

	public ConsistsOfNotFoundException(Throwable cause) {
		super(cause);
	}

	public ConsistsOfNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
