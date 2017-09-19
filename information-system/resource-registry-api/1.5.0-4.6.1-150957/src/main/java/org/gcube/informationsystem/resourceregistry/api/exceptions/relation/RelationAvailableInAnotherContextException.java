package org.gcube.informationsystem.resourceregistry.api.exceptions.relation;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContext;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class RelationAvailableInAnotherContextException extends ERAvailableInAnotherContextException implements AvailableInAnotherContext {

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
