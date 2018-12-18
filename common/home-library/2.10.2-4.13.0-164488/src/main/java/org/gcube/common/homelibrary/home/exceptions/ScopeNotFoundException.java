/**
 * 
 */
package org.gcube.common.homelibrary.home.exceptions;

/**
 * Signals when a scope is not found.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ScopeNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7180534649339990020L;

	/**
	 * Constructs a new ScopeNotFoundException with the specified message.
	 * @param msg the exception message.
	 */
	public ScopeNotFoundException(String msg) {
		super(msg);
	}

}
