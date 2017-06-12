/**
 *
 */
package org.gcube.portlets.user.geoexplorer.shared;

import javax.servlet.ServletException;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 6, 2017
 */
public class SessionExpiredException extends ServletException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1086849279224828332L;

	/**
	 *
	 */
	public SessionExpiredException() {

		super();
	}

	public SessionExpiredException(String arg0){
		super(arg0);
	}
}
