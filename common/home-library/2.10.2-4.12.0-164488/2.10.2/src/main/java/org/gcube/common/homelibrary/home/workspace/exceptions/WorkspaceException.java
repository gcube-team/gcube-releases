/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * A super class for all the workspace exceptions.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class WorkspaceException extends Exception {

	private static final long serialVersionUID = 133683823267839710L;

	/**
	 * Create a new workspace exception.
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public WorkspaceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new workspace exception.
	 * @param message the exception message.
	 */
	public WorkspaceException(String message) {
		super(message);
	}

	/**
	 * Create a new workspace exception.
	 * @param cause the exception cause.
	 */
	public WorkspaceException(Throwable cause) {
		super(cause);
	}

}
