package org.gcube.search.sru.search.adapter.commons.discoverer.exceptions;

public class SruSearchAdapterDiscoverException extends Exception {

	private static final long serialVersionUID = 1L;

	public SruSearchAdapterDiscoverException(String string, Exception e) {
		super(string, e);
	}
	
	public SruSearchAdapterDiscoverException(String string) {
		super(string);
	}
}
