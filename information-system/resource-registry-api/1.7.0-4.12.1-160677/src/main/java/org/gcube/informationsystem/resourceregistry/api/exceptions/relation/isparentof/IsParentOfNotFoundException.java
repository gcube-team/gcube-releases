package org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsParentOfNotFoundException extends RelationNotFoundException implements NotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4769773168121537127L;

	public IsParentOfNotFoundException(String message) {
		super(message);
	}

	public IsParentOfNotFoundException(Throwable cause) {
		super(cause);
	}

	public IsParentOfNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
