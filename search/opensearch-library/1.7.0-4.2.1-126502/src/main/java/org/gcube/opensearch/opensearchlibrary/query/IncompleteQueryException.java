package org.gcube.opensearch.opensearchlibrary.query;

/**
 * An exception that is thrown when one or more required query parameters are not assigned with a value
 * 
 * @author gerasimos.farantatos
 *
 */
public class IncompleteQueryException extends Exception {
	
	private static final long serialVersionUID = -1443964395346129177L;
	
	/**
	 * Creates a new instance
	 */
	public IncompleteQueryException() { 
		super();
	}

	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 */
	public IncompleteQueryException(String message) { 
		super(message);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param cause The cause of the error
	 */
	public IncompleteQueryException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 * @param cause The cause of the error
	 */
	public IncompleteQueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
