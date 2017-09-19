package org.gcube.rest.index.common.discover.exceptions;


public class IndexDiscoverException extends Exception {

	private static final long serialVersionUID = 1L;

	public IndexDiscoverException(String string, Exception e) {
		super(string, e);
	}
	
	public IndexDiscoverException(String string) {
		super(string);
	}

}
