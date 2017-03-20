package org.gcube.rest.opensearch.common.discover.exceptions;

public class OpenSearchDiscovererException extends Exception {
	private static final long serialVersionUID = 1L;

	public OpenSearchDiscovererException(String string, Exception e) {
		super(string, e);
	}
	
	public OpenSearchDiscovererException(Exception e) {
		super(e);
	}
	
	public OpenSearchDiscovererException(String string) {
		super(string);
	}

}
