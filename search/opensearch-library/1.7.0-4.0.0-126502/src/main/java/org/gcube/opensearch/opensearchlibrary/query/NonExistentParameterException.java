package org.gcube.opensearch.opensearchlibrary.query;

/**
 * An exception that is thrown when a query parameter is not present
 * 
 * @author gerasimos.farantatos
 *
 */
public class NonExistentParameterException extends Exception {
	
	private String name;
	private static final long serialVersionUID = 2981926602059901767L;

	/**
	 * Creates a new instance
	 */
	public NonExistentParameterException() { 
		super();
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 */
	public NonExistentParameterException(String message) { 
		super(message);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 * @param name The name of the query parameter related to the error
	 */
	public NonExistentParameterException(String message, String name) {
		super(message);
		this.name = name;
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param cause The cause of the error
	 */
	public NonExistentParameterException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param message The error message
	 * @param cause The cause of the error
	 */
	public NonExistentParameterException(String message, Throwable cause) {
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
