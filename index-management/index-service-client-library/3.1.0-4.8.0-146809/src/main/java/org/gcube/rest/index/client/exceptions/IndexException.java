package org.gcube.rest.index.client.exceptions;

public class IndexException extends Exception {

	private static final long serialVersionUID = 1L;

	public IndexException(String string, Exception e) {
		super(string, e);
	}

	public IndexException(String string) {
		super(string);
	}

}
