package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Argument not valid for the reader sub component operation that is undergoing
 * 
 * @author gpapanikos
 *
 */
public class GRS2ReaderInvalidArgumentException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ReaderInvalidArgumentException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ReaderInvalidArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ReaderInvalidArgumentException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
