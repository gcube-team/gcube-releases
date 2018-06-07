package org.gcube.informationsystem.resourceregistry.api.exceptions.relation;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class RelationAlreadyPresentException extends ERAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4331648416224535719L;

	public RelationAlreadyPresentException(String message) {
		super(message);
	}

	public RelationAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public RelationAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
