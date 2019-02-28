package org.gcube.informationsystem.resourceregistry.api.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class CreationException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -6223557776095431118L;

	public CreationException(String message) {
		super(message);
	}

	public CreationException(Throwable cause) {
		super(cause);
	}
	
	public CreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
