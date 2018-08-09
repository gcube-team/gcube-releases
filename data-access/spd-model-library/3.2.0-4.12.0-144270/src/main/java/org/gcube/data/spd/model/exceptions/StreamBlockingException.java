package org.gcube.data.spd.model.exceptions;

public class StreamBlockingException extends StreamException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 167182159965012187L;

	public StreamBlockingException(String repositoryName, String identifier) {
		super(repositoryName, identifier);
	}
	
	public StreamBlockingException(String repositoryName) {
		super(repositoryName, "");
	}
	
}
