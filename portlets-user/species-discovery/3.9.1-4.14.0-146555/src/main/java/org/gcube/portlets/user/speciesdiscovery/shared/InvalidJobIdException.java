/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;


/**
 * The Class InvalidJobIdException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2017
 */
public class InvalidJobIdException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -6513243962411796791L;

	/**
	 * Instantiates a new invalid job id exception.
	 */
	public InvalidJobIdException(){}

	/**
	 * Instantiates a new invalid job id exception.
	 *
	 * @param message the message
	 */
	public InvalidJobIdException(String message) {
		super(message);
	}
}
