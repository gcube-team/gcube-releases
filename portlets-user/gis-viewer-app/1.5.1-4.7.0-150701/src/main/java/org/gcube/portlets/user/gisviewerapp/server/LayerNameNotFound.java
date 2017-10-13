/**
 *
 */

package org.gcube.portlets.user.gisviewerapp.server;


/**
 * The Class LayerNameNotFound.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
public class LayerNameNotFound extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new layer name not found.
	 *
	 * @param string the string
	 */
	public LayerNameNotFound(String string) {

		super(string);
	}
}
