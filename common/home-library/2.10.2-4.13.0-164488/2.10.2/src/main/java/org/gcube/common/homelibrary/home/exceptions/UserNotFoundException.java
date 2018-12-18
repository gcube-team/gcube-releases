/**
 * 
 */
package org.gcube.common.homelibrary.home.exceptions;

/**
 * Signals that the specified user has not been found.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 1254697949032698654L;

	/**
	 * Constructs a new UserNotFoundException with the specified message.
	 * @param msg the exception message.
	 */
	public UserNotFoundException(String msg) {
		super(msg);
	}
}
