/**
 * 
 */
package org.gcube.portlets.user.messages.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 4, 2013
 *
 */
public class SessionExpiredException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 9220835668896574233L;

	/**
	 * 
	 */
	public SessionExpiredException() {
		super("Session expired");
	}
}
