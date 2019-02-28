package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions;

public class AgencyException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8490877000324199876L;

	public AgencyException (String message)
	{
		super (message);
	}

	public AgencyException (String message,Throwable cause)
	{
		super (message,cause);
	}
	
}
