/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource;

import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityCreationException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceCreationException extends EntityCreationException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 363592815430310293L;

	public ResourceCreationException(String message) {
		super(message);
	}
	
	public ResourceCreationException(Throwable cause) {
		super(cause);
	}
	
	public ResourceCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
