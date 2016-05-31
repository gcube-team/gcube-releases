package org.gcube.portlets.user.td.expressionwidget.shared.exception;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ExpressionParserException extends Exception {

	private static final long serialVersionUID = 8823457309498708839L;

	public ExpressionParserException() {
		super();
	}

	/**
	 * @param message
	 */
	public ExpressionParserException(String message) {
		super(message);
	}

	public ExpressionParserException(String message, Throwable t) {
		super(message, t);
	}

}
