/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.exceptions;


/**
 * The Class WorkspaceFolderNotFoundException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public class WorkspaceFolderNotFoundException extends WorkspaceException {

	private static final long serialVersionUID = -729077599735921718L;

	/**
	 * Instantiates a new workspace folder not found exception.
	 *
	 * @param msg the exception message.
	 */
	public WorkspaceFolderNotFoundException(String msg) {
		super(msg);
	}
}
