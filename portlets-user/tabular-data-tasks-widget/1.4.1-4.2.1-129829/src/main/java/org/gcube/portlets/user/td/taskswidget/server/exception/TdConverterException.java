/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.server.exception;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 18, 2013
 *
 */
public class TdConverterException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2018711417748593169L;
	
	/**
	 * @param string
	 */
	public TdConverterException(String ex) {
		super(ex);
	}
	
	/**
	 * 
	 */
	public TdConverterException(Exception ex) {
		super(ex);
	}

}
