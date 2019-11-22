package gr.uoa.di.madgik.grs.store.record;

/**
 * Error while accessing needed persisted information in the context of the on going record store sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2RecordStoreAccessException extends GRS2RecordStoreException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2RecordStoreAccessException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2RecordStoreAccessException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2RecordStoreAccessException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
