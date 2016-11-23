package gr.uoa.di.madgik.grs.store.buffer;

/**
 * Error while accessing needed persisted information in the context of the on going buffer store sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferStoreAccessException extends  GRS2BufferStoreException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferStoreAccessException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferStoreAccessException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2BufferStoreAccessException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
