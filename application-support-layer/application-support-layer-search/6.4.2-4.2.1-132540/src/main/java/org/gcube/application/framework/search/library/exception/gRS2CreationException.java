package org.gcube.application.framework.search.library.exception;

public class gRS2CreationException extends Exception {
	
	public gRS2CreationException(Throwable cause) {
		super("Failed to create ResultSet2 Reader", cause);
	}

	public gRS2CreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
