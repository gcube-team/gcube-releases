package org.gcube.application.framework.search.library.exception;

public class gRS2NoRecordReadWithinTimeIntervalException extends Exception{
	
	public gRS2NoRecordReadWithinTimeIntervalException() {
		super("No record read from gRS2 within the time interval specified.");
	}

}
