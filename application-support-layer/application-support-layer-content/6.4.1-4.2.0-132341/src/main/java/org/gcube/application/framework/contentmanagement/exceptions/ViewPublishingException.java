package org.gcube.application.framework.contentmanagement.exceptions;

public class ViewPublishingException extends Exception {
	
	public ViewPublishingException(Throwable cause) {
		super("Error while publishing view", cause);
	}

}
