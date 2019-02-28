package org.gcube.datacatalogue.metadatadiscovery.reader;

/**
 * The Class ApplicationProfileNotFoundException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 7, 2016
 */
@SuppressWarnings("serial")
public class MetadataProfileNotFoundException extends Exception {

 	/**
 	 * Instantiates a new application profile not found exception.
 	 *
 	 * @param message the message
 	 */
 	public MetadataProfileNotFoundException(String message) {
	    super(message);
	  }
}