/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class WrongParentTypeException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WrongParentTypeException extends WorkspaceException {

	private static final long serialVersionUID = -6202770939428067425L;

	/**
	 * Instantiates a new wrong parent type exception.
	 *
	 * @param message the exception message.
	 */
	public WrongParentTypeException(String message) {
		super(message);
	}


}
