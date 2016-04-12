package org.gcube.index.exceptions;

public class BadRequestException extends Exception {
	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super();
	}
	
	public BadRequestException(String message) {
		super(message);
	}
	
	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BadRequestException(Throwable cause) {
		super(cause);
	}
}
