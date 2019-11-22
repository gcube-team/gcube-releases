/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 4, 2013
 *
 */
public class SessionExpiredException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8423680645305738442L;

	/**
	 * 
	 */
	public SessionExpiredException() {
		super("Session expired");
	}
}
