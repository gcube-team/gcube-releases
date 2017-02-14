package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception;

/**
 * ASL Session Expired Exception
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class StatAlgoImporterSessionExpiredException  extends StatAlgoImporterServiceException {

	private static final long serialVersionUID = -4831171355042165166L;

	/**
	 * 
	 */
	public StatAlgoImporterSessionExpiredException() {
		super();
	}

	/**
	 * @param message
	 */
	public StatAlgoImporterSessionExpiredException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param t
	 */
	public StatAlgoImporterSessionExpiredException(String message,Throwable t) {
		super(message,t);
	}
	
}
