package org.gcube.usecases.ws.thredds.engine.impl;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class ProcessStatus implements Cloneable{

	public static enum Status{
		INITIALIZING, // initial status
		ONGOING, // synch in progress 
		WARNINGS,   // errors occurred, still WORKING
		STOPPED,  // STOP received, waiting for request to finish
		COMPLETED // FINISHED PROCESS
	}
	
	
	private AtomicLong queuedTransfers=new AtomicLong(0);
	private AtomicLong servedTransfers=new AtomicLong(0);
	private AtomicLong errorCount=new AtomicLong(0);
	private Status status=Status.INITIALIZING;
	
	private StringBuilder logBuilder=new StringBuilder();
	
	private String currentMessage="Waiting to start..";
	
	@Override
	protected Object clone() throws CloneNotSupportedException {	
		return super.clone();
	}

	@Override
	public String toString() {
		return "ProcessStatus [queuedTransfers=" + queuedTransfers + ", servedTransfers=" + servedTransfers
				+ ", errorCount=" + errorCount + ", status=" + status + ", currentMessage=" + currentMessage + "]";
	}
	
	public float getPercent() {
		switch(status) {
		case INITIALIZING : return 0;
		case COMPLETED : return 1;
		default : return queuedTransfers.get()==0?0:((float)(servedTransfers.get()+errorCount.get()))/queuedTransfers.get();
		}
	}
}
