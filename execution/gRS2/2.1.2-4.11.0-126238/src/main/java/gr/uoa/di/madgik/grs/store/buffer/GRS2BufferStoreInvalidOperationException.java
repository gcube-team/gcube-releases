package gr.uoa.di.madgik.grs.store.buffer;

/**
 * The operation in progress is not valid based on the status of the buffer store sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferStoreInvalidOperationException extends GRS2BufferStoreException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferStoreInvalidOperationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferStoreInvalidOperationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the error cause
	 */
	public GRS2BufferStoreInvalidOperationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
