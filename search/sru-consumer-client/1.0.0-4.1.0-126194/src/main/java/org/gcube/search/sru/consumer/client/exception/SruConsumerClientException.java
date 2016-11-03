package org.gcube.search.sru.consumer.client.exception;

public class SruConsumerClientException  extends Exception  {

	private static final long serialVersionUID = 1L;
	
	public SruConsumerClientException(String string, Exception e) {
		super(string, e);
	}
	
	public SruConsumerClientException(Exception e) {
		super(e);
	}
	
	public SruConsumerClientException(String string) {
		super(string);
	}
	
	

}
