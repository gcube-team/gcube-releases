package gr.uoa.di.madgik.grs.proxy.local;

import java.net.URI;
import java.net.URISyntaxException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;

/**
 * This proxy is an implementation of the {@link IWriterProxy} interface available to be
 * used by writers that want to make available their authored {@link IBuffer} only to
 * readers that are collocated in the same JVM as they are
 * 
 * @author gpapanikos
 *
 */
public class LocalWriterProxy implements IWriterProxy
{
	private String key=null;
	private URI locator=null;
	
	/**
	 * Creates a new instance
	 */
	public LocalWriterProxy() {}
	
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
	 * Always returns null. This type of locator does not employ any {@link IMirror} instance to 
	 * handle the synchronization between reader and writer as this is managed directly though
	 * the underlying {@link IBuffer} 
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IWriterProxy#bind()
	 */
	public IMirror bind()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The created uri uses the scheme of {@link IProxy#ProxyScheme}, "localhost" as the 
	 * host name and sets the fragment to {@link gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType#Local}. <br/> 
	 * An example of the created locator is 
	 * <code>grs2-proxy://localhost?key=13bc140f-3013-4ce1-83b4-4d57d46863b0#Local</code>
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
			this.locator = new URI(IProxy.ProxyScheme,null,"localhost",-1,null,"key="+this.key,IProxy.ProxyType.Local.toString());
		} catch (URISyntaxException e)
		{
			throw new GRS2ProxyInvalidArgumentException("Could not create locator URI",e);
		}
		return this.locator;
	}
	
	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>grs2-proxy://localhost?key=13bc140f-3013-4ce1-83b4-4d57d46863b0#Local</code>
	 * 
	 * @param locator the locator URi that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyScheme.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.Local.toString()))return false;
		return true;
	}
}
