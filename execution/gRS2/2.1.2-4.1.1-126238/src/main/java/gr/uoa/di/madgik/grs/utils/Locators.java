package gr.uoa.di.madgik.grs.utils;

import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.http.HTTPReaderProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

import java.net.URI;

/**
 * Locator utilities
 *
 * 
 * @author gerasimos.farantatos
 *
 */
public class Locators {
	
	private Locators() { }
	
	/**
	 * Converts a local locator retrieved from a {@link LocalWriterProxy} to a TCP locator which can be used in order for a remote consumer to 
	 * access the records of the producer's local {@link IBuffer}. It can be used to make the contents of a local {@link IBuffer} available to a 
	 * remote consumer, without the producer knowing that its records should be accessed in any other way other than locallyA new 
	 * {@link TCPWriterProxy} and {@link IMirror} are constructed internally, and the {@link IMirror} is bound to the local {@link IBuffer} in order
	 * for the mirroring process to start, as if the consumer had created a {@link TCPWriterProxy} in the first place.
     * <p>
     * The {@link TCPConnectionManager} must have been initialized before using this method 
	 * 
	 * @param locator The local locator, retrieved by the {@link LocalWriterProxy} of the consumer
	 * @return A TCP locator which can be used to access the contents of the local {@link IBuffer} remotely
	 * @throws GRS2ProxyInvalidArgumentException The supplied locator is malformed or not of the correct type
	 * @throws GRS2ProxyInvalidOperationException The locator could not be constructed because a proxy operation has failed
	 */
	public static URI localToTCP(URI locator) throws GRS2ProxyInvalidArgumentException, GRS2ProxyInvalidOperationException {
		if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
		if(!LocalWriterProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
		if(locator.getQuery()==null) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
		String []qs=locator.getQuery().trim().split("=");
		if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
		if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
		String key=qs[1];
		
		IBuffer buffer = GRSRegistry.Registry.getBuffer(key);
		TCPWriterProxy proxy = new TCPWriterProxy();
		proxy.setKey(key);
		buffer.setMirror(proxy.bind());
		return proxy.getLocator();
	}
	
	public static URI localToHTTP(URI locator) throws GRS2ProxyInvalidArgumentException,
	GRS2ProxyInvalidOperationException {
	if (locator == null)
		throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
	if (!LocalWriterProxy.isOfType(locator))
		throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type " + locator.toString());
	if (locator.getQuery() == null)
		throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator " + locator.toString());
	String[] qs = locator.getQuery().trim().split("=");
	if (qs.length != 2)
		throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator " + locator.toString());
	if (qs[1].trim().length() == 0)
		throw new GRS2ProxyInvalidArgumentException("Invalid key in locator " + locator.toString());
	String key = qs[1];
	
	IBuffer buffer = GRSRegistry.Registry.getBuffer(key);
	HTTPWriterProxy proxy = new HTTPWriterProxy();
	proxy.setKey(key);
	buffer.setMirror(proxy.bind());
	return proxy.getLocator();
}

public static boolean isGRS2Locator(URI locator) {
return (LocalReaderProxy.isOfType(locator) || HTTPStoreReaderProxy.isOfType(locator)
		|| HTTPReaderProxy.isOfType(locator) || TCPReaderProxy.isOfType(locator)
		|| LocalStoreReaderProxy.isOfType(locator) || TCPStoreReaderProxy.isOfType(locator));
}
}
