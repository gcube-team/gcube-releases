package org.gcube.search.exceptions;

public class SearchClientException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SearchClientException(String string) {
		super(string);
	}

	public SearchClientException(String string, Exception e) {
		super(string, e);
	}
	
	public SearchClientException(Exception e) {
		super(e);
	}

}
