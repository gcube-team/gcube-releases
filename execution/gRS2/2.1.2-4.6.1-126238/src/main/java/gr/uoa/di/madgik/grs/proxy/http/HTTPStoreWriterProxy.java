package gr.uoa.di.madgik.grs.proxy.http;

import gr.uoa.di.madgik.commons.server.http.HTTPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreFactory;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * This proxy class although it implements the tagging {@link IProxy} interface, it does not
 * implement, as the name suggests, the {@link IWriterProxy} interface. The reason for this is that
 * it is meant to be used for a special case of {@link IBuffer} publishing which is through the
 * buffer store utility provided. This proxy class can be used to create a locator URI pointing to
 * a stored {@link IBuffer} that contains all the {@link Record}s available through the provided
 * URI locators. 
 * 
 * @author Alex Antoniadis
 *
 */
public class HTTPStoreWriterProxy implements IProxy
{
	
	/**
	 * This method instantiates an {@link IBufferStore} through the {@link BufferStoreFactory}, sets the 
	 * respective information provided, initializes the {@link IBufferStore}, registers the new store
	 * and creates a locator URI that can be used to access the {@link IBufferStore}. The created locator
	 * is of the following type <code>http://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#HTTPStore</code>
	 * and uses the scheme defined in {@link IProxy#ProxyStoreScheme}. The hostname and port used are the ones
	 * available through {@link HTTPConnectionManager#GetConnectionManagerHostName()} and 
	 * {@link HTTPConnectionManager#GetConnectionManagerPort()}
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
			String hostname=HTTPConnectionManager.GetConnectionManagerHostName();
			int port=HTTPConnectionManager.GetConnectionManagerPort();
			store=BufferStoreFactory.getManager();
			store.setLocators(locators);
			store.setMultiplexType(multiplex);
			store.setReaderTimeout(timeout);
			store.setReaderTimeoutTimeUnit(unit);
			store.initialize();
			store.store();
			String key=GRSRegistry.Registry.add(store);
			store.setKey(key);
			return new URI(IProxy.ProxyType.HTTP.toString() ,null,hostname,port,null,"key="+key,IProxy.ProxyType.HTTPStore.toString());
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
