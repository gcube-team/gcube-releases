package org.gcube.data.spd.model.exceptions;

public class StreamNonBlockingException extends StreamException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3889030287274902179L;

	public StreamNonBlockingException(String repositoryName, String identifier) {
		super(repositoryName, identifier);
	}

	public StreamNonBlockingException(String repositoryName) {
		super(repositoryName, "");
	}
	
}
