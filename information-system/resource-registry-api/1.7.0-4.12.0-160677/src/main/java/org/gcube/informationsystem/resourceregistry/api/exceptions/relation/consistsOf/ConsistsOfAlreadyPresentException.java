package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ConsistsOfAlreadyPresentException extends RelationAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1557370304405327949L;

	public ConsistsOfAlreadyPresentException(String message) {
		super(message);
	}

	public ConsistsOfAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public ConsistsOfAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
