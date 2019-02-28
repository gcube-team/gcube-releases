/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WrongItemTypeException extends WorkspaceException {

	private static final long serialVersionUID = -5013356582135921634L;

	/**
	 * @param message the exception message.
	 */
	public WrongItemTypeException(String message) {
		super(message);
	}
}
