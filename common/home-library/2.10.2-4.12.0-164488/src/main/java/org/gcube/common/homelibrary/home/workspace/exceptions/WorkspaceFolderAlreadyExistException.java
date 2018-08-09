/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceFolderAlreadyExistException extends WorkspaceException {

	private static final long serialVersionUID = -6285161417298571997L;

	/**
	 * @param message the exception message.
	 */
	public WorkspaceFolderAlreadyExistException(String message) {
		super(message);
	}

}
