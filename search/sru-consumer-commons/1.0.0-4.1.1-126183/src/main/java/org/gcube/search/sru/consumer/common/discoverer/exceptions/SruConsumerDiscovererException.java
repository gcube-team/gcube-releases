package org.gcube.search.sru.consumer.common.discoverer.exceptions;

public class SruConsumerDiscovererException extends Exception {

	private static final long serialVersionUID = 1L;

	public SruConsumerDiscovererException(String string, Exception e) {
		super(string, e);
	}
	
	public SruConsumerDiscovererException(String string) {
		super(string);
	}
}
