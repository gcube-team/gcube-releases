package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.exceptions;

public class InvalidInformationSystemDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5752488842249479549L;

	public InvalidInformationSystemDataException (String message)
	{
		super (message);
	}
	
	public InvalidInformationSystemDataException (String message,Throwable cause)
	{
		super (message,cause);
	}
	
}
