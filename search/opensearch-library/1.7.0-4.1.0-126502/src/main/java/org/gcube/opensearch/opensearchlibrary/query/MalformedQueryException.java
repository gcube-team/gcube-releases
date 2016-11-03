package org.gcube.opensearch.opensearchlibrary.query;

/**
 * An exception that is thrown when an error in a query construct is detected, e.g. an invalid parameter value
 * 
 * @author gerasimos.farantatos
 *
 */
public class MalformedQueryException extends Exception {
	
	private static final long serialVersionUID = 5139006563968001870L;
	private String name;
	
	/**
	 * Creates a new instance
	 */
	public MalformedQueryException() { 
		super();
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 */
	public MalformedQueryException(String message) { 
		super(message);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 * @param name The name of the query parameter related to the error
	 */
	public MalformedQueryException(String message, String name) {
		super(message);
		this.name = name;
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param cause The cause of the error
	 */
	public MalformedQueryException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 * @param cause The cause of the error
	 */
	public MalformedQueryException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Retrieves the name of the parameter related to the error
	 * 
	 * @return The name of the parameter
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a short description of this exception. The description is similar to that of an {@link Exception} also including
	 * the query parameter name related to the error, if it is specified.
	 * 
	 * @return A string representation of the exception
	 */
	@Override
	public String toString() {
		if(name == null)
			return super.toString();
		return super.toString() + ". Parameter: " + name;
	}

}