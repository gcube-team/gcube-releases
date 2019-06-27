/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * A super class for all the workspace exceptions.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WorkspaceException extends Exception{


	/**
	 *
	 */
	private static final long serialVersionUID = -5657714325026850145L;

	/**
	 *
	 */
	public WorkspaceException() {

	}
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
