package org.gcube.application.framework.search.library.exception;

public class gRS2ReaderException extends Exception{
	
	public gRS2ReaderException(Throwable cause) {
		super("Could not read results.", cause);
	}

}
