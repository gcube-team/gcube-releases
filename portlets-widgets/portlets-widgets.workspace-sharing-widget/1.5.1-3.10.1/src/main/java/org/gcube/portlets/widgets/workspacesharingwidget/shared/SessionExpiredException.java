/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.shared;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 3, 2014
 *
 */
public class SessionExpiredException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4706569045335951710L;

	/**
	 * 
	 */
	public SessionExpiredException() {
		super("Server session expired");
	}
}
