package org.gcube.datacatalogue.ckanutillibrary.server.exceptions;

/**
 * Too many clusters in this scope exception.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class TooManyRunningClustersException extends Exception {

	private static final long serialVersionUID = -7847493730006647045L;
	private static final String DEFAULT_MESSAGE = "Too many CKan data catalague instances for this scope!";

	public TooManyRunningClustersException(){
		super(DEFAULT_MESSAGE);
	}

	public TooManyRunningClustersException(String message) {
		super(message);
	}

}
