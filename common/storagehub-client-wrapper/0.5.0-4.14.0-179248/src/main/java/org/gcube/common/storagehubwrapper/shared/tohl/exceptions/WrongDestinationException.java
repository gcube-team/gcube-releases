/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class WrongDestinationException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WrongDestinationException extends WorkspaceException {

	private static final long serialVersionUID = -1765035358366830708L;

	/**
	 * Instantiates a new wrong destination exception.
	 *
	 * @param message the exception message.
	 */
	public WrongDestinationException(String message) {
		super(message);
	}


}
