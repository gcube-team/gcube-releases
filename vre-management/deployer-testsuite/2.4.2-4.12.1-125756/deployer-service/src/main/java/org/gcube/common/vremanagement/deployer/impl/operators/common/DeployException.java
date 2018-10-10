/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.operators.common;

/**
 * Custom exception to report deployment troubles
 * 
 * @author Manuele Simi
 *
 */
@SuppressWarnings("serial")
public class DeployException extends Exception {

	/**
	 * Creates a new Deploy Exception
	 */
	public DeployException() {		
	}

	/**
	 * @param message the error message
	 */
	public DeployException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause of the exception
	 */
	public DeployException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the error message
	 * @param cause the cause of the exception
	 */
	public DeployException(String message, Throwable cause) {
		super(message, cause);
	}

}
