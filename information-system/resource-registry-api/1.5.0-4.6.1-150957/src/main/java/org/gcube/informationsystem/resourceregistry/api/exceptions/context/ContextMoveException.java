package org.gcube.informationsystem.resourceregistry.api.exceptions.context;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ContextMoveException extends ContextException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 3978134061057784201L;

	public ContextMoveException(String message) {
		super(message);
	}
	
	public ContextMoveException(Throwable cause) {
		super(cause);
	}
	
	public ContextMoveException(String message, Throwable cause) {
		super(message, cause);
	}

}
