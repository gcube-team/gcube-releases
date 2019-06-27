/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class InsufficientPrivilegesException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
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
