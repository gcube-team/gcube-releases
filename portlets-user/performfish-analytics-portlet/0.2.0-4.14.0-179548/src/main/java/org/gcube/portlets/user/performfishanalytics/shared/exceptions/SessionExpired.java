/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.exceptions;

/**
 * The Class SessionExpired.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 11, 2019
 */
public class SessionExpired extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 756417673620945244L;

	/**
	 * Instantiates a new session expired.
	 */
	public SessionExpired() {

	}

	/**
	 * Instantiates a new session expired.
	 *
	 * @param arg0 the arg0
	 */
	public SessionExpired(String arg0){
		super(arg0);
	}
}
