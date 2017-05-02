package org.gcube.rest.index.client.exceptions;

public class BadCallException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadCallException(String string, Exception e) {
		super(string, e);
	}

	public BadCallException(String string) {
		super(string);
	}

}