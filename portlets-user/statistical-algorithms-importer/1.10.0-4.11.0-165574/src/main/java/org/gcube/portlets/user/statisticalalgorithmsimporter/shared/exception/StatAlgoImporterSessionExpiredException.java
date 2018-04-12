package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author Giancarlo Panichi
 *
 */
public class StatAlgoImporterSessionExpiredException extends StatAlgoImporterServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public StatAlgoImporterSessionExpiredException() {
		super();
	}

	/**
	 * @param message
	 *            message
	 */
	public StatAlgoImporterSessionExpiredException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 *            message
	 * @param throwable
	 *            throwable
	 */
	public StatAlgoImporterSessionExpiredException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
