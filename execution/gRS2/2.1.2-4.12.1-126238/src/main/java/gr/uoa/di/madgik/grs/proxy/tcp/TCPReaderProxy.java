package gr.uoa.di.madgik.grs.proxy.tcp;

import java.net.URI;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.proxy.IReaderProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalReaderProxy;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.tcp.mirror.TCPReaderMirror;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

/**
 * This proxy is an implementation of the {@link IReaderProxy} interface available to be
 * used by readers that want to access an {@link IBuffer} made available by a writer
 * that is not necessarily collocated in the same JVM as they are. This proxy enables access
 * to a local or remote host through a raw TCP connection
 * 
 * @author gpapanikos
 *
 */
public class TCPReaderProxy implements IReaderProxy
{
	private String key=null;
	private URI locator=null;
	private String hostname=null;
	private int port=-1;
	private IMirror mirror=null;
	private boolean overrideBufferCapacity=false;
	private int bufferCapacity=-1;
	
	/**
	 * Retrieves the hostname where the {@link TCPWriterProxy} is located and is serving the consumed {@link IBuffer}
	 * 
	 * @return the hostname
	 */
	public String getHostname()
	{
		return this.hostname;
	}
	
	/**
	 * Retrieves the port that the {@link TCPWriterProxy} is using to serve the consumed {@link IBuffer}
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
	 * <p>
	 * This method parses the provided URI to retrieve the needed information. An example of the URI
	 * this proxy can manage is <code>grs2-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#TCP</code>
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the proxy has already been initialized
	 * @throws GRS2ProxyInvalidArgumentException if the provided locator is null or empty, or the parsed information is not valid
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#fromLocator(java.net.URI)
	 */
	public void fromLocator(URI locator) throws GRS2ProxyInvalidOperationException, GRS2ProxyInvalidArgumentException
	{
		if(this.locator!=null) throw new GRS2ProxyInvalidOperationException("Proxy already initialized with locator");
		if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
		if(!TCPReaderProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
		if(locator.getQuery()==null) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
		String []qs=locator.getQuery().trim().split("=");
		if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
		if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
		this.hostname=locator.getHost();
		this.port=locator.getPort();
		this.key=qs[1];
		this.locator=locator;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Instructs the {@link TCPReaderMirror} that will be initialized in a subsequent operation to override its buffer capacity
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the operation is performed after buffer initialization
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#overrideBufferCapacity(int)
	 */
	@Override
	public void overrideBufferCapacity(int capacity) throws GRS2ProxyInvalidOperationException
	{
		if(this.mirror!=null) throw new GRS2ProxyInvalidOperationException("Cannot override buffer capacity after buffer initialization");
		this.overrideBufferCapacity=true;
		this.bufferCapacity=capacity;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Initializes a {@link TCPReaderMirror} to handle the reader side mirroring procedure. The mirror is set with the
	 * information available to the proxy, using the {@link TCPReaderMirror#setKey(String)}, {@link TCPReaderMirror#setPort(int)}
	 * and {@link TCPReaderMirror#setHostname(String)} and then the {@link TCPReaderMirror#handle()} method is invoked. The proxy
	 * waits for the mirroring procedure to be initialized blocking using {@link TCPReaderMirror#waitInitialization()} and if
	 * there was any error during the initialization procedure, it is retrieved and thrown using {@link TCPReaderMirror#getInitializationError()}.
	 * After this procedure is completed, the mirroring procedure continues in the {@link TCPReaderMirror} thread of execution. The
	 * returned {@link IBuffer} is the one available though {@link TCPReaderMirror#getBuffer()} and is populated from {@link TCPReaderMirror}
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if the provided locator that populated this instance has not set the needed parameters
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.IReaderProxy#getBuffer()
	 */
	public IBuffer getBuffer() throws GRS2ProxyInvalidOperationException
	{
		if(this.key==null || this.key.trim().length()==0 || this.locator==null) throw new GRS2ProxyInvalidOperationException("Proxy not correctly initialized");
		if(this.mirror==null)
		{
			try
			{
				TCPReaderMirror mr=new TCPReaderMirror();
				mr.setKey(this.key);
				mr.setPort(this.port);
				mr.setHostname(this.hostname);
				if(this.overrideBufferCapacity) mr.overrideBufferCapacity(this.bufferCapacity);
				mr.handle();
				if(!mr.waitInitialization()) throw mr.getInitializationError();
				IBuffer buf=mr.getBuffer();
				this.mirror=mr;
				buf.setMirror(this.mirror);
			}catch(GRS2ProxyMirrorException ex)
			{
				throw new GRS2ProxyInvalidOperationException("Could not initialize mirror process", ex);
			}
		}
		return this.mirror.getBuffer();
	}
	
	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>grs2-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#TCP</code>
	 * 
	 * @param locator the locator URi that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyScheme.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.TCP.toString()))return false;
		return true;
	}

}
