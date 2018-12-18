package gr.uoa.di.madgik.grs.proxy;

import gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreReaderProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;

/**
 * Tagging interface used by the specific {@link IProxy} implementations and the extending 
 * {@link IReaderProxy} and {@link IWriterProxy} interfaces
 * 
 * @author gpapanikos
 *
 */
public interface IProxy
{
	/**
	 * The scheme for the gRS2 proxy locator URIs
	 */
	public static final String ProxyScheme="grs2-proxy";
	/**
	 * The scheme for the gRS2 Store proxy locator URIs
	 */
	public static final String ProxyStoreScheme="grs2-store-proxy";
	
	/**
	 * The recognized and supported proxy types
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum ProxyType
	{
		/**
		 * A local proxy supported through {@link LocalReaderProxy} and {@link LocalWriterProxy}
		 */
		Local,
		/**
		 * A TCP proxy supported through {@link TCPReaderProxy} and {@link TCPWriterProxy}
		 */
		TCP,
		/**
		 * A local store proxy supported through {@link LocalStoreReaderProxy} and {@link LocalStoreWriterProxy}
		 */
		LocalStore,
		/**
		 * A TCP store proxy supported through {@link TCPStoreReaderProxy} and {@link TCPStoreWriterProxy}
		 */
		TCPStore,
		
		HTTP,
		HTTPStore
	}
}
