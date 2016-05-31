package org.gcube.rest.opensearch.client.exception;

public class OpenSearchClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenSearchClientException(String string, Exception e) {
		super(string, e);
	}
	
	public OpenSearchClientException(Exception e) {
		super(e);
	}

	public OpenSearchClientException(String string) {
		super(string);
	}

}
