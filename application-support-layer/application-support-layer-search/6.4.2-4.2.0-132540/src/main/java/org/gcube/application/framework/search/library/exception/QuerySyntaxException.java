package org.gcube.application.framework.search.library.exception;

public class QuerySyntaxException extends Exception {
	
	public QuerySyntaxException(String msg) {
		super("Error while submitting search query to Search Master");
	}
}
