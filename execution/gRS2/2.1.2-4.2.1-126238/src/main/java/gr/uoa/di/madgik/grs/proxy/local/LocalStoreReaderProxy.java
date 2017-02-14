package gr.uoa.di.madgik.grs.proxy.local;

import java.net.URI;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreReader;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

/**
 * This proxy extends the {@link LocalReaderProxy} capabilities by adding an additional logic layer
 * on top of the original extended base capabilities. This additional logic layer serves to contact the
 * {@link IBufferStore} that can then create a locator capable of being managed by the underlying
 * {@link LocalReaderProxy} instance. 
 * 
 * @author gpapanikos
 *
 */
public class LocalStoreReaderProxy extends LocalReaderProxy
{
	private boolean storeContacted=false;

	/**
	 * Creates a new instance
	 */
	public LocalStoreReaderProxy()
	{
		this.storeContacted=false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method parses the provided URI locator that is expected to be in the form of the following example
	 * <code>grs2-store-proxy://localhost?key=e064140f-4bcc-4c84-94c6-9420f8f31b05#LocalStore</code>. After the
	 * parsing is completed, a {@link BufferStoreReader} instance is created, and the {@link BufferStoreReader#populate()}
	 * method is invoked. The created locator from this operation is then forwarded to the underlying
	 * {@link LocalReaderProxy} to take over any subsequent actions 
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidArgumentException If the provided locator is not of the expected type of the parsing procedure
	 * does not provide the valid information needed
	 * @throws GRS2ProxyInvalidOperationException if there was a problem initializing the underlying {@link LocalReaderProxy}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy#fromLocator(java.net.URI)
	 */
	public void fromLocator(URI locator) throws GRS2ProxyInvalidArgumentException, GRS2ProxyInvalidOperationException 
	{
		BufferStoreReader storeReader=null;
		try
		{
			if(this.storeContacted) super.fromLocator(locator);
			else
			{
				this.storeContacted=true;
				if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
				if(!LocalStoreReaderProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
				String []qs=locator.getQuery().trim().split("=");
				if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
				if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
				String key=qs[1];
				LocalWriterProxy newProxy=new LocalWriterProxy();
				storeReader=new BufferStoreReader(key, newProxy);
				URI storeAccessLocator=storeReader.populate();
				this.fromLocator(storeAccessLocator);
			}
		}catch(GRS2Exception ex)
		{
			if(storeReader!=null) try{ storeReader.dispose(); }catch(Exception e){}
			throw new GRS2ProxyInvalidOperationException("Could not initialize proxy", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method should only be used after the initialization step of the {@link LocalStoreReaderProxy#fromLocator(URI)}
	 * has been completed. After this is done, any call to this method simple forwards the request to he underlying
	 * {@link LocalReaderProxy#getBuffer()} method
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if a call to this overriding method is done even after the
	 * the store has been contacted and the underlying {@link LocalReaderProxy} needs to be used.
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy#getBuffer()
	 */
	public IBuffer getBuffer() throws GRS2ProxyInvalidOperationException
	{
		if(!this.storeContacted) throw new GRS2ProxyInvalidOperationException("Method not supported for this proxy type");
		return super.getBuffer();
	}

	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>grs2-store-proxy://localhost?key=e064140f-4bcc-4c84-94c6-9420f8f31b05#LocalStore</code>
	 * 
	 * @param locator the locator URI that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyStoreScheme.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.LocalStore.toString()))return false;
		return true;
	}
}
