package org.gcube.datacatalogue.ckanutillibrary.shared.ex;

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
