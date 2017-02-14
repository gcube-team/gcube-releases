package gr.uoa.di.madgik.grs.proxy.local;

import java.net.URI;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IReaderProxy;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

/**
 * This proxy is an implementation of the {@link IReaderProxy} interface available to be
 * used by readers that want to access an {@link IBuffer} made available by a writer
 * that is collocated in the same JVM as they are
 * 
 * @author gpapanikos
 *
 */
public class LocalReaderProxy implements IReaderProxy
{
	private String key=null;
	private URI locator=null;
	
	/**
	 * Creates a new instance
	 */
	public LocalReaderProxy() {}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method parses the provided URI to retrieve the needed information. An example of the URI
	 * this proxy can manage is <code>grs2-proxy://localhost?key=13bc140f-3013-4ce1-83b4-4d57d46863b0#Local</code>
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the proxy has already been initialized
	 * @throws GRS2ProxyInvalidArgumentException if the provided locator is null or empty, or 
	 * the parsed information is not valid
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#fromLocator(java.net.URI)
	 */
	public void fromLocator(URI locator) throws GRS2ProxyInvalidArgumentException, GRS2ProxyInvalidOperationException
	{
		if(this.locator!=null) throw new GRS2ProxyInvalidOperationException("Proxy already initialized with locator");
		if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
		if(!LocalReaderProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
		String []qs=locator.getQuery().trim().split("=");
		if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
		if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
		this.key=qs[1];
		this.locator=locator;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Performs no action, as the {@link LocalReaderProxy} retrieves the {@link IBuffer} directly from the
	 * local {@link GRSRegistry}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#overrideBufferCapacity(int)
	 */
	@Override
	public void overrideBufferCapacity(int capacity) 
	{
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Retrieves the {@link IBuffer} directly from the local {@link GRSRegistry}. If no {@link IBuffer}
	 * is found associated with the provided key, null is returned
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the provided locator that populated this 
	 * instance has not set the needed parameters
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#getBuffer()
	 */
	public IBuffer getBuffer() throws GRS2ProxyInvalidOperationException
	{
		if(this.key==null || this.key.trim().length()==0 || this.locator==null) throw new GRS2ProxyInvalidOperationException("Proxy not correctly initialized");
		return GRSRegistry.Registry.getBuffer(this.key);
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
