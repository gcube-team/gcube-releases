/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class ItemNotFoundException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class ItemNotFoundException extends WorkspaceException {

	private static final long serialVersionUID = -553175277581696583L;

	/**
	 * Instantiates a new item not found exception.
	 *
	 * @param message the exception message.
	 */
	public ItemNotFoundException(String message) {
		super(message);
	}

}
