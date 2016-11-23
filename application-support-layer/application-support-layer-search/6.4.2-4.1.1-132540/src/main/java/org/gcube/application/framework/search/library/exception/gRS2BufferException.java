package org.gcube.application.framework.search.library.exception;

public class gRS2BufferException extends Exception{
	
	public gRS2BufferException(Throwable e) {
		super("Could not read results", e);
	}

}
