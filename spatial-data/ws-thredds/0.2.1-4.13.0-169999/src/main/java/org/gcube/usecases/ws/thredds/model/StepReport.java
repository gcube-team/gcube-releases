package org.gcube.usecases.ws.thredds.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StepReport {

	public static enum Status{
		ERROR,OK,CANCELLED
	}
	
	
	public static enum OperationType{
		WS_TO_TH,TH_TO_WS,DELETE_REMOTE
	}
	
	private String elementName;
	private String message;
	private Status status;
	private OperationType operationType;
	private long completionTime;
	
}
