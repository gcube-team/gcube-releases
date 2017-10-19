package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityNotFoundException;


/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class FacetNotFoundException extends EntityNotFoundException implements NotFound {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1665901586630603463L;

	public FacetNotFoundException(String message) {
		super(message);
	}

	public FacetNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public FacetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
