/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class StatAlgoImporterServiceException extends Exception {

	private static final long serialVersionUID = -2255657546267656458L;

	/**
	 * 
	 */
	public StatAlgoImporterServiceException() {
		super();
	}

	/**
	 * @param message
	 *            message
	 */
	public StatAlgoImporterServiceException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            message
	 * @param throwable
	 *            throwable
	 * 
	 */
	public StatAlgoImporterServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
