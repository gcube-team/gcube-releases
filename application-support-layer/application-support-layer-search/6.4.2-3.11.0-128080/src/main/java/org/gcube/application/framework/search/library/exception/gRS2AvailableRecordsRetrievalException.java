package org.gcube.application.framework.search.library.exception;

public class gRS2AvailableRecordsRetrievalException extends Exception{
	public gRS2AvailableRecordsRetrievalException(Throwable cause) {
		super("Could not retrieve available records' number from buffer.", cause);
	}
}
