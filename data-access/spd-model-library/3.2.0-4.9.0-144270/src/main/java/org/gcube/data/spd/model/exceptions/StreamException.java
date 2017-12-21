package org.gcube.data.spd.model.exceptions;

public abstract class StreamException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6254059644228632966L;

	private String repositoryName;
	private String identifier;
	
	public StreamException(String repositoryName, String identifier) {
		super();
		this.repositoryName = repositoryName;
		this.identifier = identifier;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public String getIdentifier() {
		return identifier;
	}
			
}
