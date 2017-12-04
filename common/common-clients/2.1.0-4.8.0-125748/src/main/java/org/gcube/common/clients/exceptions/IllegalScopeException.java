package org.gcube.common.clients.exceptions;

/**
 * A {@link ServiceException} raised when attempt to contact service endpoints outside the scope.
 * 
 * @author Fabio Simeoni
 *
 */
public class IllegalScopeException extends InvalidRequestException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 */
	public IllegalScopeException(){
		super();
	}
	
	/**
	 * Creates an instance with a given message.
	 * @param msg the message
	 */
	public IllegalScopeException(String msg) {
		super(msg);
	}
}
