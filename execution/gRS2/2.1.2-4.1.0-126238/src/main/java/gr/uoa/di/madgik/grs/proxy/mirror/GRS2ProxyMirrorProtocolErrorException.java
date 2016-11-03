package gr.uoa.di.madgik.grs.proxy.mirror;

/**
 * There was a protocol error in the proxy mirror operation
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyMirrorProtocolErrorException extends GRS2ProxyMirrorException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyMirrorProtocolErrorException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyMirrorProtocolErrorException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyMirrorProtocolErrorException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
