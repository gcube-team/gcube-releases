package org.gcube.application.framework.contentmanagement.exceptions;

public class ServiceEPRRetrievalException extends Exception{
	
	public ServiceEPRRetrievalException(Throwable cause) {
		super("Error while retrieving service epr from IS", cause);
	}
}
