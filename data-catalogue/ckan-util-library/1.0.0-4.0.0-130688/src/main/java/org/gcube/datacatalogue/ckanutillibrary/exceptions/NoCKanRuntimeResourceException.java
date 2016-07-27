package org.gcube.datacatalogue.ckanutillibrary.exceptions;

/**
 * No elasticsearch cluster in the infrastructure found exception.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class NoCKanRuntimeResourceException extends Exception {
	
	private static final long serialVersionUID = -40748130477807648L;
	
	private static final String DEFAULT_MESSAGE = "No CKan catalogue instance for this scope!";

	public NoCKanRuntimeResourceException(){
		super(DEFAULT_MESSAGE);
	}

	public NoCKanRuntimeResourceException(String message) {
		super(message);
	}
}
