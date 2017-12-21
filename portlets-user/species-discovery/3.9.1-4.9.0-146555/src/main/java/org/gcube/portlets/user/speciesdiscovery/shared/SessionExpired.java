/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 17, 2017
 */
public class SessionExpired extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = -4412298198084979081L;

	/**
	 *
	 */
	public SessionExpired() {

	}

	public SessionExpired(String arg0){
		super(arg0);
	}
}
