/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;

/**
 * The Class WrongItemTypeException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WrongItemTypeException extends WorkspaceException {

	private static final long serialVersionUID = -5013356582135921634L;

	/**
	 * Instantiates a new wrong item type exception.
	 *
	 * @param message the exception message.
	 */
	public WrongItemTypeException(String message) {
		super(message);
	}
}
