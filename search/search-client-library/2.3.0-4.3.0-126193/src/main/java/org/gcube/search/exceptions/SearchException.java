package org.gcube.search.exceptions;

public class SearchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SearchException(String string) {
		super(string);
	}

	public SearchException(String string, Throwable e) {
		super(string, e);
	}

	
}
