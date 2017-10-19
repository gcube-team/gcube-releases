package gr.uoa.di.madgik.grs.proxy;

import gr.uoa.di.madgik.grs.GRS2Exception;

/**
 * Base exception for all error that are though by the proxy sub component 
 * 
 * @author gpapanikos
 *
 */
public class GRS2ProxyException extends GRS2Exception
{

	private static final long serialVersionUID = 8683542572315875098L;
	
	/**
	 * Create a new instance
	 */
	public GRS2ProxyException()
	{
		super();
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 */
	public GRS2ProxyException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new instance
	 * 
	 * @param message the error message
	 * @param cause the cause of the error
	 */
	public GRS2ProxyException(String message,Throwable cause)
	{
		super(message,cause);
	}

}
