package org.gcube.rest.index.common.entities;

public class ExternalEndpointException extends Exception{

	private static final long serialVersionUID = 5734934942411999724L;

	public ExternalEndpointException(String Message)
	{
		super(Message);
	}

	public ExternalEndpointException(String Message,Throwable cause)
	{
		super(Message,cause);
	}
}