package gr.uoa.di.madgik.grs.proxy;

/**
 * Argument not valid for the proxy sub component operation that is undergoing
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyInvalidArgumentException extends GRS2ProxyException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyInvalidArgumentException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyInvalidArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyInvalidArgumentException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
