/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.operators.common;

/**
 * @author manuele simi (CNR)
 *
 */
public class PackageAldreadyDeployedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @param message
	 */
	public PackageAldreadyDeployedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PackageAldreadyDeployedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PackageAldreadyDeployedException(String message, Throwable cause) {
		super(message, cause);
	}

}
