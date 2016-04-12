package gr.uoa.di.madgik.grs.buffer;

/**
 * Initialization related error of the buffer sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferInitializationException extends GRS2BufferException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance 
	 */
	public GRS2BufferInitializationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferInitializationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2BufferInitializationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
