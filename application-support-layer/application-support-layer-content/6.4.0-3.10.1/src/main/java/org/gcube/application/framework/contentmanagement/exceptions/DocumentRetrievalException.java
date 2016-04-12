package org.gcube.application.framework.contentmanagement.exceptions;

public class DocumentRetrievalException extends Exception{

	public DocumentRetrievalException(Throwable cause) {
		super("An error occured while reading document", cause);
	}
}
