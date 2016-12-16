/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.operators.ant;

/**
 * Exception class for Ant interaction troubles
 * @author Manuele Simi
 *
 */
@SuppressWarnings("serial")
public class AntInterfaceException extends Exception {

	/**
	 * 
	 *
	 */
	public AntInterfaceException() {

	}

	/**
	 * @param message the error message
	 */
	public AntInterfaceException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause of the exception
	 */
	public AntInterfaceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the error message
	 * @param cause the cause of the exception
	 */
	public AntInterfaceException(String message, Throwable cause) {
		super(message, cause);		
	}

}
