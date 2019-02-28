package gr.uoa.di.madgik.grs.buffer;

/**
 * The operation in progress is not valid based on the status of the buffer sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferInvalidOperationException extends GRS2BufferException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferInvalidOperationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferInvalidOperationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the error cause
	 */
	public GRS2BufferInvalidOperationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
