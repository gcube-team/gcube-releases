package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAlreadyPresentException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsParentOfAlreadyPresentException extends RelationAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -244592605626665740L;

	public IsParentOfAlreadyPresentException(String message) {
		super(message);
	}

	public IsParentOfAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public IsParentOfAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
