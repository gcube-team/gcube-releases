/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
	 */
	public StatAlgoImporterServiceException(String message) {
		super(message);
	}
	
	
	public StatAlgoImporterServiceException(String message,Throwable t) {
		super(message,t);
	}
	

}
