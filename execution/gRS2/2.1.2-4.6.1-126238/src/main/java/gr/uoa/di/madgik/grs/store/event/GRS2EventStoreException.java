package gr.uoa.di.madgik.grs.store.event;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Base exception for all error that are though by the event store sub component 
 * 
 * @author gpapanikos
 *
 */
public class GRS2EventStoreException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2EventStoreException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2EventStoreException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2EventStoreException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
