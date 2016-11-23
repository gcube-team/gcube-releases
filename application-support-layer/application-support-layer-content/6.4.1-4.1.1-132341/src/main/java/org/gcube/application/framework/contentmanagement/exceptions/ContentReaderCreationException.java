package org.gcube.application.framework.contentmanagement.exceptions;

public class ContentReaderCreationException extends Exception {
	
	public ContentReaderCreationException(Throwable cause) {
		super("An error occured while creating CM reader", cause);
	}

}
