package org.gcube.datatransformation.client.library.exceptions;

public class DTSClientException extends Exception {
	private static final long serialVersionUID = 1L;

	public DTSClientException(String string) {
		super(string);
	}

	public DTSClientException(String string, Throwable e) {
		super(string, e);
	}

}
