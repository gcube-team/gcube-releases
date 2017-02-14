package org.gcube.search.sru.db.common.discoverer.exceptions;

public class SruDBDiscoverException extends Exception {

	private static final long serialVersionUID = 1L;

	public SruDBDiscoverException(String string, Exception e) {
		super(string, e);
	}
	
	public SruDBDiscoverException(String string) {
		super(string);
	}
}
