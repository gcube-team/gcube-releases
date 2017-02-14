package org.gcube.application.framework.search.library.exception;

public class QuerySubmissionSearchException extends Exception {
	
	public QuerySubmissionSearchException(Throwable cause) {
		super("Error while submitting search query to Search Master", cause);
	}

}
