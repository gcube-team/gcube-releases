package gr.uoa.di.madgik.grs.proxy;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.http.HTTPReaderProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreReaderProxy;

import java.net.URI;

/**
 * This utility class instantiates the appropriate {@link IReaderProxy} implementation that can
 * serve a reader to access the {@link IBuffer} managed through the {@link IWriterProxy} that
 * created the provided URI
 * 
 * @author gpapanikos
 *
 */
public class ProxyFactory
{
	
	/**
	 * Instantiates the appropriate {@link IReaderProxy} that can manage the provided URI locator.
	 * After the proxy is instantiated, the {@link IReaderProxy#fromLocator(URI)} method is
	 * invoked to populate the proxy with the needed information provided by the URI 
	 * 
	 * @param locator The locator to use
	 * @return the instantiated {@link IReaderProxy}
	 * @throws GRS2ProxyException the proxy could not be instantiated
	 */
	public static IReaderProxy getProxy(URI locator) throws GRS2ProxyException
	{
		IReaderProxy proxy=null;
		if(LocalReaderProxy.isOfType(locator)) proxy=new LocalReaderProxy();
		
		else if(HTTPStoreReaderProxy.isOfType(locator)) proxy=new HTTPStoreReaderProxy();
		else if(HTTPReaderProxy.isOfType(locator)) proxy=new HTTPReaderProxy();
		
		else if(TCPReaderProxy.isOfType(locator)) proxy=new TCPReaderProxy();
		else if(LocalStoreReaderProxy.isOfType(locator)) proxy=new LocalStoreReaderProxy();
		else if(TCPStoreReaderProxy.isOfType(locator)) proxy=new TCPStoreReaderProxy();
		if(proxy==null) throw new GRS2ProxyInvalidArgumentException("Could not recognize provided locator of " + locator.toString());
		proxy.fromLocator(locator);
		return proxy;
	}

}
