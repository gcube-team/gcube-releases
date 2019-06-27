package org.gcube.data.analysis.excel.engine.exceptions;

public class ExcelNotSavedException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2305662002627243291L;

	public ExcelNotSavedException (String message)
	{
		super (message);
	}
	
	public ExcelNotSavedException (String message, Throwable cause)
	{
		super (message,cause);
	}
}
