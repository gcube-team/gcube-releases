package org.gcube.smartgears.handlers.application.request;


/**
 * Thrown for the occurrence of an error during request processing.
 * 
 * @author Fabio Simeoni
 *
 */
public class RequestException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final RequestError error;
	
	/**
	 * Creates an instance with an underlying error.
	 * @param error the error
	 */
	public RequestException(RequestError error) {
		this(error, error.message());
	}
	
	/**
	 * Creates an instance with an underling error and a custom message.
	 * @param message the message
	 * @param error the error
	 */
	public RequestException(RequestError error,String message) {
		super(message);
		this.error=error;
	}
	
	/**
	 * Creates an instance with an underlying error and an underlying cause
	 * @param error the error
	 * @param cause the cause;
	 */
	public RequestException(RequestError error,Throwable cause) {
		this(error, cause, error.message());
	}
	
	/**
	 * Creates an instance with an underlying error, an underlying cause, and an underlying message.
	 * @param error the error
	 * @param cause the cause;
	 * @Param message the message;
	 */
	public RequestException(RequestError error,Throwable cause,String message) {
		super(message,cause);
		this.error=error;
	}
	
	/**
	 * Returns the underlying error.
	 * @return the error
	 */
	public RequestError error() {
		return error;
	}
	
}
