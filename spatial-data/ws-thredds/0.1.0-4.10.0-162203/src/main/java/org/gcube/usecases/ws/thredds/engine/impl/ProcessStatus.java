package org.gcube.usecases.ws.thredds.engine.impl;

public class ProcessStatus {

	public static enum Status{
		INITIALIZING, // initial status
		ONGOING, // synch in progress 
		ERROR,   // errors occurred
		STOPPED  // STOP received, waiting for request to finish
	}
	
	
	private long queuedTransfers;
	private long servedTransfers;
	private long errorCount;
	private Status status;
}
