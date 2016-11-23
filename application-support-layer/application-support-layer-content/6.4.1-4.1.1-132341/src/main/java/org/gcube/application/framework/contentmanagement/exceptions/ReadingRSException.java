package org.gcube.application.framework.contentmanagement.exceptions;

public class ReadingRSException extends Exception{
	
	public ReadingRSException(Throwable cause) {
		super("Error while reading ResultSet", cause);
	}

}
