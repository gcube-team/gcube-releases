package org.gcube.informationsystem.resourceregistry.api.exceptions.entity;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class FacetNotFoundException extends EntityException implements ObjectNotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1687373446724146351L;

	public FacetNotFoundException(String message) {
		super(message);
	}

}
