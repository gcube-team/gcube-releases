package org.gcube.data.spd.model.exceptions;

public class RetrySameStreamException extends StreamNonBlockingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RetrySameStreamException(String repositoryName, String identifier) {
		super(repositoryName, identifier);
	}
	
}
