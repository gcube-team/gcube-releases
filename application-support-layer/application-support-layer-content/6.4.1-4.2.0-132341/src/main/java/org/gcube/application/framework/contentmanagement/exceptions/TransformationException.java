package org.gcube.application.framework.contentmanagement.exceptions;

public class TransformationException extends Exception {
	
	public TransformationException(Throwable cause) {
		super("Error while transforming data", cause);
	}

}
