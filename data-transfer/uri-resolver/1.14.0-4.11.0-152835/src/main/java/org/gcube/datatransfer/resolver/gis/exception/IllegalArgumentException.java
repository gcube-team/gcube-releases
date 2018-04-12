/**
 *
 */
package org.gcube.datatransfer.resolver.gis.exception;


/**
 * The Class IllegalArgumentException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class IllegalArgumentException extends Exception {


	/**
	 *
	 */
	private static final long serialVersionUID = 8589705350737964325L;

	/**
	 * Instantiates a new illegal argument exception.
	 */
	public IllegalArgumentException() {
		super();
	}

    /**
     * Instantiates a new illegal argument exception.
     *
     * @param message the message
     */
    public IllegalArgumentException(String message) {
        super(message);
    }

}
