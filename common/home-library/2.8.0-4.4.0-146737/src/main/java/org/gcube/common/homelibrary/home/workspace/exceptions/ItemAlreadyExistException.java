/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ItemAlreadyExistException extends WorkspaceException {

	private static final long serialVersionUID = -2884454770219837164L;

	/**
	 * @param message the exception message.
	 */
	public ItemAlreadyExistException(String message) {
		super(message);
	}

}
