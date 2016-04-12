package org.gcube.datatransfer.resolver.applicationprofile;

/**
 * The Class ApplicationProfileNotFoundException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 14, 2016
 */
@SuppressWarnings("serial")
public class ApplicationProfileNotFoundException extends Exception {
	 
 	/**
 	 * Instantiates a new application profile not found exception.
 	 *
 	 * @param message the message
 	 */
 	public ApplicationProfileNotFoundException(String message) {
	    super(message);
	  }
}