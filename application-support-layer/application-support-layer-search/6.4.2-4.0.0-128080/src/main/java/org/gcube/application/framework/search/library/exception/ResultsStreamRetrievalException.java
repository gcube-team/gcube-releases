package org.gcube.application.framework.search.library.exception;

public class ResultsStreamRetrievalException extends Exception {
	
	public ResultsStreamRetrievalException(Throwable cause) {
		super("Could not retrieve the Results Stream from Search Service", cause);
	}
	
	public ResultsStreamRetrievalException(String message, Throwable cause) {
		super(message,cause);
	}
	
}
