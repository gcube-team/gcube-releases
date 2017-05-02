package gr.uoa.di.madgik.grs.store.event;

/**
 * Error while accessing needed persisted information in the context of the on going event store sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2EventStoreAccessException extends GRS2EventStoreException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2EventStoreAccessException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2EventStoreAccessException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2EventStoreAccessException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
