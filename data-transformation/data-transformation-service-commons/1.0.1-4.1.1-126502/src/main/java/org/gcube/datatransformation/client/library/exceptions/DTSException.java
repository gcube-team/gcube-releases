package org.gcube.datatransformation.client.library.exceptions;

public class DTSException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DTSException(String string) {
		super(string);
	}

	public DTSException(String string, Throwable e) {
		super(string, e);
	}

}
