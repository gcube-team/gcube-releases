package org.gcube.portlets.user.workspace.server.reader;

/**
 * The Class ApplicationProfileException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 6, 2016
 */
@SuppressWarnings("serial")
public class ApplicationProfileNotFoundException extends Exception {

 	/**
 	 * Instantiates a new application profile exception.
 	 *
 	 * @param message the message
 	 */
 	public ApplicationProfileNotFoundException(String message) {
	    super(message);
	  }
}