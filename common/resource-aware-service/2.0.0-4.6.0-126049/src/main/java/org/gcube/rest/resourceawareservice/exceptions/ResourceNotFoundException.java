package org.gcube.rest.resourceawareservice.exceptions;

public class ResourceNotFoundException extends Exception {

	public ResourceNotFoundException(Exception e) {
		super(e);
	}
	
	public ResourceNotFoundException(String string, Exception e) {
		super(string, e);
	}
	
	public ResourceNotFoundException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}