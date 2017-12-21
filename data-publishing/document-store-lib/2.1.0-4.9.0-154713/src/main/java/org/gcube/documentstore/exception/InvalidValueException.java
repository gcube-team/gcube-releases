package org.gcube.documentstore.exception;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class InvalidValueException extends Exception {

	/**
	 * Generated serial Version UID
	 */
	private static final long serialVersionUID = 4403699127526286772L;

	public InvalidValueException() {
		super();
	}

	public InvalidValueException(String message) {
		super(message);
	}
	
	public InvalidValueException(Throwable cause) {
		super(cause);
	}
	
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
