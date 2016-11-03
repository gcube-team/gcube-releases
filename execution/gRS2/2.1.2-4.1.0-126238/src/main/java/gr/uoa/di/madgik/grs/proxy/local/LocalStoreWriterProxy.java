package gr.uoa.di.madgik.grs.proxy.local;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreFactory;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

/**
 * This proxy class although it implements the tagging {@link IProxy} interface, it does not
 * implement, as the name suggests, the {@link IWriterProxy} interface. The reason for this is that
 * it is meant to be used for a special case of {@link IBuffer} publishing which is through the
 * buffer store utility provided. This proxy class can be used to create a locator URI pointing to
 * a stored {@link IBuffer} that contains all the {@link Record}s available through the provided
 * URI locators. 
 * 
 * @author gpapanikos
 *
 */
public class LocalStoreWriterProxy implements IProxy
{
	
	/**
	 * This method instantiates an {@link IBufferStore} through the {@link BufferStoreFactory}, sets the 
	 * respective information provided, initializes the {@link IBufferStore}, registers the new store
	 * and creates a locator URI that can be used to access the {@link IBufferStore}. The created locator
	 * is of the following type <code>grs2-store-proxy://localhost?key=e064140f-4bcc-4c84-94c6-9420f8f31b05#LocalStore</code>
	 * and uses the scheme defined in {@link IProxy#ProxyStoreScheme} 
	 * 
	 * @param locators The locators forwarded to the {@link IBufferStore#setLocators(URI[])}
	 * @param multiplex The multiplex type forwarded to {@link IBufferStore#setMultiplexType(gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType)}
	 * @param timeout The timeout forwarded to {@link IBufferStore#setReaderTimeout(long)}
	 * @param unit The timeout time unit forwarded to {@link IBufferStore#setReaderTimeoutTimeUnit(TimeUnit)}
	 * @return the locator URI to the {@link IBufferStore} serving the new {@link IBuffer}
	 * @throws GRS2BufferStoreException the {@link IBufferStore} could not be initialized
	 * @throws GRS2ProxyInvalidArgumentException the locator URI could not be created
	 */
	public static URI store(URI []locators, IBufferStore.MultiplexType multiplex, long timeout, TimeUnit unit) throws GRS2BufferStoreException, GRS2ProxyInvalidArgumentException
	{
		IBufferStore store=null;
		try
		{
			store=BufferStoreFactory.getManager();
			store.setLocators(locators);
			store.setMultiplexType(multiplex);
			store.setReaderTimeout(timeout);
			store.setReaderTimeoutTimeUnit(unit);
			store.initialize();
			store.store();
			String key=GRSRegistry.Registry.add(store);
			store.setKey(key);
			return new URI(IProxy.ProxyStoreScheme,null,"localhost",-1,null,"key="+key,IProxy.ProxyType.LocalStore.toString());
		}catch(GRS2BufferStoreException ex)
		{
			if(store!=null){try{store.dispose();}catch(Exception e){}}
			throw ex;
		} catch (URISyntaxException exx)
		{
			if(store!=null){try{store.dispose();}catch(Exception e){}}
			throw new GRS2ProxyInvalidArgumentException("Could not create store uri", exx);
		}
	}
}
