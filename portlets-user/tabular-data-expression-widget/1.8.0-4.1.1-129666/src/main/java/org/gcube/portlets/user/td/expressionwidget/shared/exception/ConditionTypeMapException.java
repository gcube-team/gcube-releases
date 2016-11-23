package org.gcube.portlets.user.td.expressionwidget.shared.exception;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ConditionTypeMapException extends Exception {

	private static final long serialVersionUID = -9066034060104406559L;

	/**
	 * 
	 */
	public ConditionTypeMapException() {
		super();
	}

	/**
	 * @param message
	 */
	public ConditionTypeMapException(String message) {
		super(message);
	}
	
	
	public ConditionTypeMapException(String message,Throwable t) {
		super(message,t);
	}
	

}

