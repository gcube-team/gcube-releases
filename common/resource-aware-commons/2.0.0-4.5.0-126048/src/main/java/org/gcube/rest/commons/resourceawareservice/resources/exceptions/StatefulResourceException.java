package org.gcube.rest.commons.resourceawareservice.resources.exceptions;

public class StatefulResourceException extends Exception {

	public StatefulResourceException(String string, Exception e) {
		super(string, e);
	}
	
	public StatefulResourceException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
