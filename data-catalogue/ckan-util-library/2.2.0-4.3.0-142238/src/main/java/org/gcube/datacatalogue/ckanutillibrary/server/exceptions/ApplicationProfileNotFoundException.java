package org.gcube.datacatalogue.ckanutillibrary.server.exceptions;

@SuppressWarnings("serial")
/**
 * Thrown when no application profile with such information is found
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ApplicationProfileNotFoundException extends Exception {
	 public ApplicationProfileNotFoundException(String message) {
	    super(message);
	  }
}
