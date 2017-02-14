package gr.uoa.di.madgik.grs.proxy.mirror;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Base exception for all error that are though by the proxy mirror sub component 
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyMirrorException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyMirrorException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyMirrorException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyMirrorException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
