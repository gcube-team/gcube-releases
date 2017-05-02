package gr.uoa.di.madgik.grs.proxy.mirror;

/**
 * The operation in progress is not valid based on the status of the proxy mirror sub component
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyMirrorInvalidOperationException extends GRS2ProxyMirrorException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyMirrorInvalidOperationException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyMirrorInvalidOperationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyMirrorInvalidOperationException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
