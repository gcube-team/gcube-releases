package org.gcube.informationsystem.resourceregistry.api.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class NotFoundException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1687373446724146351L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}
	
	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
