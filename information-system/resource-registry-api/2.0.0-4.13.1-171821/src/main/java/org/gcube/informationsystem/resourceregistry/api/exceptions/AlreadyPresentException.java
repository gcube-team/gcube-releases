package org.gcube.informationsystem.resourceregistry.api.exceptions;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class AlreadyPresentException extends ResourceRegistryException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1129094888903457750L;

	public AlreadyPresentException(String message) {
		super(message);
	}
	
	public AlreadyPresentException(Throwable cause) {
		super(cause);
	}
	
	public AlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

}
