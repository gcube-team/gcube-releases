package gr.uoa.di.madgik.grs.buffer;

/**
 * Exception indicating that the buffer is already disposed and no additional actions can be performed on it
 * 
 * @author gpapanikos
 *
 */
public class GRS2BufferDisposedException extends GRS2BufferException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2BufferDisposedException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2BufferDisposedException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2BufferDisposedException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
