package org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource;

import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresent;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;


/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ResourceAlreadyPresentException extends EntityAlreadyPresentException implements AlreadyPresent {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 4412436343971879115L;

	public ResourceAlreadyPresentException(String message) {
		super(message);
	}

	public ResourceAlreadyPresentException(Throwable cause) {
		super(cause);
	}
	
	public ResourceAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}
}
