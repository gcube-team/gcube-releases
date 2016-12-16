package org.gcube.search.sru.search.adapter.client.exception;

public class SruSearchAdapterClientException  extends Exception  {

	private static final long serialVersionUID = 1L;
	
	public SruSearchAdapterClientException(String string, Exception e) {
		super(string, e);
	}
	
	public SruSearchAdapterClientException(Exception e) {
		super(e);
	}
	
	public SruSearchAdapterClientException(String string) {
		super(string);
	}
	
	

}
