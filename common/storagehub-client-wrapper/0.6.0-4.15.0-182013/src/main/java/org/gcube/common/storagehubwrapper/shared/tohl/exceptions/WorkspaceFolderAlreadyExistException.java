/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;

/**
 * The Class WorkspaceFolderAlreadyExistException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WorkspaceFolderAlreadyExistException extends WorkspaceException {

	private static final long serialVersionUID = -6285161417298571997L;

	/**
	 * Instantiates a new workspace folder already exist exception.
	 *
	 * @param message the exception message.
	 */
	public WorkspaceFolderAlreadyExistException(String message) {
		super(message);
	}

}
