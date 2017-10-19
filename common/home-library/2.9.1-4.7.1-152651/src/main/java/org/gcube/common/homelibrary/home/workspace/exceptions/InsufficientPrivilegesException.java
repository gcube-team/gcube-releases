/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class InsufficientPrivilegesException extends WorkspaceException {

	private static final long serialVersionUID = 2423742342338757212L;
	
	/**
	 * Create a new internal error exception.
	 * @param cause the exception cause.
	 */
	public InsufficientPrivilegesException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new internal error exception.
	 * @param message the exception message.
	 */
	public InsufficientPrivilegesException(String message) {
		super(message);
	}

	/**
	 * Create a new internal error exception.
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public InsufficientPrivilegesException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
