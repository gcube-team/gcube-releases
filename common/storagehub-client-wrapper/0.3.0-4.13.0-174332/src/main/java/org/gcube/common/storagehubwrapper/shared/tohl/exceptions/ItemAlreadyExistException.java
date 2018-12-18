/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class ItemAlreadyExistException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class ItemAlreadyExistException extends WorkspaceException {

	private static final long serialVersionUID = -2884454770219837164L;

	/**
	 * Instantiates a new item already exist exception.
	 *
	 * @param message the exception message.
	 */
	public ItemAlreadyExistException(String message) {
		super(message);
	}

}
