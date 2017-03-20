package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;


/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class FacetAlreadyPresentException extends EntityAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5515068278908488769L;

	public FacetAlreadyPresentException(String message) {
		super(message);
	}
	
	public FacetAlreadyPresentException(Throwable cause) {
		super(cause);
	}
	
	public FacetAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
