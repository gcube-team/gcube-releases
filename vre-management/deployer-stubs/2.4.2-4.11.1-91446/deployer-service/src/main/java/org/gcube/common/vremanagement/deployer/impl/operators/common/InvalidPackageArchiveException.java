/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.operators.common;

/**
 * Exception class to report an invalid package format
 * 
 * @author Manuele Simi
 *
 */
@SuppressWarnings("serial")
public class InvalidPackageArchiveException extends Exception {
	/**
	 * 
	 */
	public InvalidPackageArchiveException() {
		
	}

	/**
	 * @param message the error message
	 */
	public InvalidPackageArchiveException(String message) {
		super(message);
		
	}

	/**
	 * @param cause the cause of the exception
	 */
	public InvalidPackageArchiveException(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @param message the error message
	 * @param cause the cause of the exception
	 */
	public InvalidPackageArchiveException(String message, Throwable cause) {
		super(message, cause);		
	}

}
