package org.gcube.rest.resourcemanager.publisher;

public class ResourcePublisherException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourcePublisherException() {

	}

	public ResourcePublisherException(String string, Throwable e) {
		super(string, e);
	}
	
	public ResourcePublisherException(String string) {
		super(string);
	}
	
	public ResourcePublisherException(Throwable e) {
		super(e);
	}

}
