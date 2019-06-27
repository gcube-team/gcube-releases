package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans;

public class SDMXDataResultBean 
{
	public enum RESULT {
		OK,
		ERROR,
		WARNING;
	}

	private RESULT result;
	private StringBuilder messageBuilder;
	private Throwable exception;

	
	public SDMXDataResultBean ()
	{
		this.result = RESULT.OK;
		this.messageBuilder = new StringBuilder();
	}
	
	public RESULT getResult() {
		return result;
	}
	
	public void setError (boolean setError)
	{
		if (setError) this.result = RESULT.ERROR;
		
	}
	
	public String getMessage() {
		return messageBuilder.toString();
	}
	public void addMessage(String message) {
		
		if (this.result == RESULT.ERROR) this.messageBuilder = new StringBuilder();
		else if (this.result == RESULT.OK)  this.result = RESULT.WARNING;
		
		this.messageBuilder.append(message).append(' ');
		
		 
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	
}
