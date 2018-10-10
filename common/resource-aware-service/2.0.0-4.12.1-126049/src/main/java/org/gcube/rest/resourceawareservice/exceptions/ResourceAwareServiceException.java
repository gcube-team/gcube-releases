package org.gcube.rest.resourceawareservice.exceptions;

public class ResourceAwareServiceException extends Exception {

	public ResourceAwareServiceException(Exception e) {
		super(e);
	}
	
	public ResourceAwareServiceException(String string, Exception e) {
		super(string, e);
	}
	
	public ResourceAwareServiceException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
