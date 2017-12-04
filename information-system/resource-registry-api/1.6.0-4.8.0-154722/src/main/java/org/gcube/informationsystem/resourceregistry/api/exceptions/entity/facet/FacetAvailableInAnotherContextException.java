package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContext;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAvailableInAnotherContextException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FacetAvailableInAnotherContextException extends EntityAvailableInAnotherContextException implements AvailableInAnotherContext {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7502387344011649559L;

	public FacetAvailableInAnotherContextException(String message) {
		super(message);
	}

	public FacetAvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}
	
	public FacetAvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
