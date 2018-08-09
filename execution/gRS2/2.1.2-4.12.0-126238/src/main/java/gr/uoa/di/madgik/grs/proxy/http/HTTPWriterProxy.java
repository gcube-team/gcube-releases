package gr.uoa.di.madgik.grs.proxy.http;

import gr.uoa.di.madgik.commons.server.http.HTTPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.mirror.HTTPWriterMirror;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This proxy is an implementation of the {@link IWriterProxy} interface available to be
 * used by writers that want to make available their authored {@link IBuffer} to readers 
 * that can connect to an HTTP connection that can serve it regardless of their location
 * 
 * @author Alex Antoniadis
 *
 */
public class HTTPWriterProxy implements IWriterProxy
{
	private String key=null;
	private URI locator=null;
	private String hostname=null;
	private int port=-1;
	private IMirror mirror=null;
	
	/**
	 * Creates a new instance
	 */
	public HTTPWriterProxy()
	{
		this.hostname=HTTPConnectionManager.GetConnectionManagerHostName();
		this.port=HTTPConnectionManager.GetConnectionManagerPort();
	}
	
	/**
	 * The name of the host this {@link HTTPWriterProxy} is used. The hostname is retrieved by 
	 * {@link HTTPConnectionManager#GetConnectionManagerHostName()} 
	 * 
	 * @return the host name
	 */
	public String getHostname()
	{
		return this.hostname;
	}
	
	/**
	 * The port the connection manager used. The port is retrieved by 
	 * {@link HTTPConnectionManager#GetConnectionManagerPort()} 
	 * 
	 * @return the port
	 */
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the key is already set for this instance
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IWriterProxy#setKey(java.lang.String)
	 */
	public void setKey(String key) throws GRS2ProxyInvalidOperationException
	{
		if(this.locator!=null) throw new GRS2ProxyInvalidOperationException("Key for locator already set");
		this.key=key;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * An instance of the {@link HTTPWriterMirror} that can serve in publishing the {@link IBuffer} is created if not already
	 * available and returned
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the key i not set for this instance
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IWriterProxy#bind()
	 */
	public IMirror bind() throws GRS2ProxyInvalidOperationException
	{
		if(this.key==null || this.key.trim().length()==0) throw new GRS2ProxyInvalidOperationException("Buffer key is not set");
		if(this.mirror==null) this.mirror=new HTTPWriterMirror();
		return mirror;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The created uri uses the scheme of {@link IProxy#ProxyScheme}, the host name and port returned from
	 * {@link HTTPWriterProxy#getHostname()} and {@link HTTPWriterProxy#getPort()} and sets the fragment to {@link ProxyType#HTTP}. <br/> 
	 * An example of the created locator is <code>grs2-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#HTTP</code>
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the set key is null or empty
	 * @throws GRS2ProxyInvalidArgumentException if there was a problem creating the locator URI
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IWriterProxy#getLocator()
	 */
	public URI getLocator() throws GRS2ProxyInvalidOperationException, GRS2ProxyInvalidArgumentException
	{
		if(this.locator!=null) return this.locator;
		if(this.key==null || this.key.trim().length()==0) throw new GRS2ProxyInvalidOperationException("Buffer key is not set");
		try
		{
			this.locator = new URI("http",null,hostname,port,null,"key="+this.key,IProxy.ProxyType.HTTP.toString());
		} catch (URISyntaxException e)
		{
			throw new GRS2ProxyInvalidArgumentException("Could not create locator URI",e);
		}
		return this.locator;
	}
	
	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>grs2-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#HTTP</code>
	 * 
	 * @param locator the locator URI that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyScheme.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.HTTP.toString()))return false;
		return true;
	}


}
