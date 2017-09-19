/**
 * 
 */
package org.gcube.common.homelibrary.home.data.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class FileAlreadyExistException extends Exception {

	private static final long serialVersionUID = -3628060909336387699L;

	/**
	 * @param message the exception message.
	 */
	public FileAlreadyExistException(String message) {
		super(message);
	}
}
