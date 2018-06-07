/**
 *
 */
package org.gcube.portlets.user.workspace.server;


/**
 * The Class FileNotVersionedException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2017
 */
public class FileNotVersionedException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = -954314398414781437L;


	/**
	 * Instantiates a new file not versioned exception.
	 */
	FileNotVersionedException(){}

	/**
	 * Instantiates a new file not versioned exception.
	 *
	 * @param string the string
	 */
	public FileNotVersionedException(String string) {

		super(string);
	}
}
