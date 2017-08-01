package gr.uoa.di.madgik.grs;

/**
 * Base exception for all errors occurring within the gRS2 component
 * 
 * @author gpapanikos
 *
 */
public class GRS2Exception extends Exception
{

	private static final long serialVersionUID = 5800275539048828237L;
	
	/**
	 * Create a new instance
	 */
	public GRS2Exception()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2Exception(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2Exception(String message,Throwable cause)
	{
		super(message,cause);
	}

}
