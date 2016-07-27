/**
 * 
 */
package org.gcube.common.homelibrary.consistency;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CheckException extends Exception {

	private static final long serialVersionUID = 2475849246467278331L;

	/**
	 * @param message the exception message.
	 */
	public CheckException(String message) {
		super(message);
	}

	/**
	 * @param message the exception message.
	 * @param cause the exception cause.
	 */
	public CheckException(String message, Throwable cause) {
		super(message, cause);
	}
}
