package gr.uoa.di.madgik.grs.proxy.mirror;

/**
 * The proxy mirror is already in dispose
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyMirrorDisposedException extends GRS2ProxyMirrorException
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyMirrorDisposedException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyMirrorDisposedException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyMirrorDisposedException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
