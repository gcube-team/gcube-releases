package gr.uoa.di.madgik.grs.buffer;

/**
 * Argument not valid for the buffer sub component operation that is undergoing
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferInvalidArgumentException extends GRS2BufferException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferInvalidArgumentException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferInvalidArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2BufferInvalidArgumentException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
