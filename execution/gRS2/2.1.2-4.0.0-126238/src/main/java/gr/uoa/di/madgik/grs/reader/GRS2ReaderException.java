package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Base exception for all error that are though by the reader sub component 
 * 
 * @author gpapanikos
 *
 */
public class GRS2ReaderException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ReaderException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ReaderException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ReaderException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
