/**
 * 
 */
package org.gcube.common.homelibrary.home.data.exceptions;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class FolderAlreadyExistException extends Exception {

	private static final long serialVersionUID = 3934381687937566511L;

	/**
	 * @param message the exception message.
	 */
	public FolderAlreadyExistException(String message) {
		super(message);
	}

}
