package org.gcube.data.analysis.tabulardata.expression;

public class NotEvaluableDataTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5791875728909251178L;

	public NotEvaluableDataTypeException() {
		super();
	}

	public NotEvaluableDataTypeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotEvaluableDataTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotEvaluableDataTypeException(String message) {
		super(message);
	}

	public NotEvaluableDataTypeException(Throwable cause) {
		super(cause);
	}


	
}
