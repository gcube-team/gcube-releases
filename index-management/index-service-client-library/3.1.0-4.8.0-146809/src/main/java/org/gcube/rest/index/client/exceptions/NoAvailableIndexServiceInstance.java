package org.gcube.rest.index.client.exceptions;

public class NoAvailableIndexServiceInstance extends Exception {

	private static final long serialVersionUID = -1161086448733455290L;

	public NoAvailableIndexServiceInstance(String string, Exception e) {
		super(string, e);
	}

	public NoAvailableIndexServiceInstance(String string) {
		super(string);
	}
	
}
