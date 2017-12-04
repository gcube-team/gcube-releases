package org.gcube.datatransfer.resolver.gis.property;

/**
 * The Class PropertyFileNotFoundException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 13, 2016
 */
@SuppressWarnings("serial")
public class PropertyFileNotFoundException extends Exception {
	 
 	/**
 	 * Instantiates a new property file not found exception.
 	 *
 	 * @param message the message
 	 */
 	public PropertyFileNotFoundException(String message) {
	    super(message);
	  }
}