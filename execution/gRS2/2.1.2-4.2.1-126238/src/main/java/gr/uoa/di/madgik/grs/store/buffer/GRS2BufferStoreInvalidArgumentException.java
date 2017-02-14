package gr.uoa.di.madgik.grs.store.buffer;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Argument not valid for the buffer store sub component operation that is undergoing
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferStoreInvalidArgumentException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferStoreInvalidArgumentException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferStoreInvalidArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2BufferStoreInvalidArgumentException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
