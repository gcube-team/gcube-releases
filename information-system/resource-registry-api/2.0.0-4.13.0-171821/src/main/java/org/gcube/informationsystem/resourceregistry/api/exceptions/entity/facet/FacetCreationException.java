/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet;

import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityCreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FacetCreationException extends EntityCreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3065194750535027873L;
	
	public FacetCreationException(String message) {
		super(message);
	}
	
	public FacetCreationException(Throwable cause) {
		super(cause);
	}
	
	public FacetCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
