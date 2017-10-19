package org.gcube.informationsystem.resourceregistry.api.exceptions.relation;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class RelationNotFoundException extends ERNotFoundException implements NotFound {


	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5236597489824997797L;

	public RelationNotFoundException(String message) {
		super(message);
	}

	public RelationNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public RelationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
