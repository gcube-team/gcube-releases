package org.gcube.index.exceptions;

public class InternalServerErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InternalServerErrorException() {
		super();
	}
	
	public InternalServerErrorException(String message) {
		super(message);
	}
	
	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InternalServerErrorException(Throwable cause) {
		super(cause);
	}

}
