/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 14, 2014
 *
 */
public class ColumnNotTypedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ColumnNotTypedException() {
		super();
	}

	/**
	 * @param string
	 */
	public ColumnNotTypedException(String string) {
		super(string);
	}
}
