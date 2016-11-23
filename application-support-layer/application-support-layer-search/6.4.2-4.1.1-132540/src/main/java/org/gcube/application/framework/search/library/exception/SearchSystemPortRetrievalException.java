package org.gcube.application.framework.search.library.exception;

public class SearchSystemPortRetrievalException extends Exception {

	public SearchSystemPortRetrievalException(Throwable cause) {
		super("Failed to retrieve port for Search Master", cause);
	}
}
