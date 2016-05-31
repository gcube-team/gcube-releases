package gr.uoa.di.madgik.grs.writer;

/**
 * Argument not valid for the writer sub component operation that is undergoing
 * 
 * @author gpapanikos
 *
 */
public class GRS2WriterInvalidArgumentException extends GRS2WriterException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2WriterInvalidArgumentException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2WriterInvalidArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2WriterInvalidArgumentException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
