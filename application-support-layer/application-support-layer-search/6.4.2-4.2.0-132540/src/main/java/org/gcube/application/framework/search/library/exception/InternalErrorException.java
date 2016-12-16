package org.gcube.application.framework.search.library.exception;

public class InternalErrorException extends Exception{

	private static final long serialVersionUID = 1L;

	public InternalErrorException(Throwable cause) {
		super("Internal error occurred.", cause);
	}

}
