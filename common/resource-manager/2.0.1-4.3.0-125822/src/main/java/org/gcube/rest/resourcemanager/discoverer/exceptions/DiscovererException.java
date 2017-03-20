package org.gcube.rest.resourcemanager.discoverer.exceptions;

public class DiscovererException extends Exception {

	private static final long serialVersionUID = 1L;

	public DiscovererException(String string, Exception e) {
		super(string, e);
	}
	
	public DiscovererException(Exception e) {
		super(e);
	}

	public DiscovererException(String string) {
		super(string);
	}

}
