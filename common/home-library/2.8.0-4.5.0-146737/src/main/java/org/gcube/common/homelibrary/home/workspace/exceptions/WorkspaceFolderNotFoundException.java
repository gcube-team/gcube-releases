/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceFolderNotFoundException extends WorkspaceException {

	private static final long serialVersionUID = -729077599735921718L;

	/**
	 * @param msg the exception message.
	 */
	public WorkspaceFolderNotFoundException(String msg) {
		super(msg);
	}
}
