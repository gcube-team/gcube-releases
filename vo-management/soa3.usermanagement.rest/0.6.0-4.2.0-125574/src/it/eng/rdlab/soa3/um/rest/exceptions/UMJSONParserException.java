package it.eng.rdlab.soa3.um.rest.exceptions;

/**
 * Indicates a serious JSON parsing error.
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 *
 */
public class UMJSONParserException extends RuntimeException{


	private static final long serialVersionUID = 4053985229840261184L;

	/**
	 * Wrap an existing exception in a UMJSONParserException.
	 * 
	 * @param message The error to use the message from the embedded exception.
	 * @param cause Any exception
	 */
	public UMJSONParserException(String message, Throwable cause) {

			super(message, cause);
		    }

}
