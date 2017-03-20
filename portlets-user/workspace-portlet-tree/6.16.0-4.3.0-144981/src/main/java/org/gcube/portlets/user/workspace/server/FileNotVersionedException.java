/**
 *
 */
package org.gcube.portlets.user.workspace.server;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2017
 */
public class FileNotVersionedException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = -954314398414781437L;


	FileNotVersionedException(){}
	/**
	 * @param string
	 *
	 */
	public FileNotVersionedException(String string) {

		super(string);
	}
}
