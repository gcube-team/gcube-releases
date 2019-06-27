package org.gcube.informationsystem.resourceregistry.api.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class AvailableInAnotherContextException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7499131763751652582L;

	public AvailableInAnotherContextException(String message) {
		super(message);
	}
	
	public AvailableInAnotherContextException(Throwable cause) {
		super(cause);
	}
	
	public AvailableInAnotherContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
