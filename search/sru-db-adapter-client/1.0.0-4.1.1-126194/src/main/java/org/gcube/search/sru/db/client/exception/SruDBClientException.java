package org.gcube.search.sru.db.client.exception;

public class SruDBClientException  extends Exception  {

	private static final long serialVersionUID = 1L;
	
	public SruDBClientException(String string, Exception e) {
		super(string, e);
	}
	
	public SruDBClientException(Exception e) {
		super(e);
	}
	
	public SruDBClientException(String string) {
		super(string);
	}
	
	

}
