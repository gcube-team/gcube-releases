package gr.uoa.di.madgik.grs.proxy;

/**
 * The operation in progress is not valid based on the status of the proxy sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyInvalidOperationException extends GRS2ProxyException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyInvalidOperationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyInvalidOperationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the error cause
	 */
	public GRS2ProxyInvalidOperationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
