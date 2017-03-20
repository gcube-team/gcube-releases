package gr.uoa.di.madgik.grs.proxy.tcp;

import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry.NamedEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * This proxy extends the {@link TCPReaderProxy} capabilities by adding an additional logic layer
 * on top of the original extended base capabilities. This additional logic layer serves to contact the
 * {@link IBufferStore} that can then create a locator capable of being managed by the underlying
 * {@link TCPReaderProxy} instance. 
 * 
 * @author gpapanikos
 *
 */
public class TCPStoreReaderProxy extends TCPReaderProxy
{
	private boolean storeContacted=false;
	
	/**
	 * Creates a new instance
	 */
	public TCPStoreReaderProxy()
	{
		this.storeContacted=false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method parses the provided URI locator that is expected to be in the form of the following example
	 * <code>grs2-store-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#TCPStore</code>. After the
	 * parsing is completed, a connection is made to the provided host and port where it is expected that an instance
	 * of the {@link TCPStoreConnectionHandler} is listening. The key of the {@link IBufferStore} is send and the URI 
	 * locator is retrieved which is then forwarded to the underlying {@link TCPReaderProxy} to take over any subsequent actions 
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidArgumentException If the provided locator is not of the expected type of the parsing procedure
	 * does not provide the valid information needed
	 * @throws GRS2ProxyInvalidOperationException if there was a problem initializing the underlying {@link TCPReaderProxy}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.tcp.TCPReaderProxy#fromLocator(java.net.URI)
	 */
	public void fromLocator(URI locator) throws GRS2ProxyInvalidArgumentException, GRS2ProxyInvalidOperationException
	{
		Socket socket=null;
		ObjectInputStream  in=null;
		ObjectOutputStream out=null;
		try
		{
			if(this.storeContacted)super.fromLocator(locator);
			else
			{
				this.storeContacted=true;
				if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
				if(!TCPStoreReaderProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
				if(locator.getQuery()==null) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
				String []qs=locator.getQuery().trim().split("=");
				if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
				if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
				String hostname=locator.getHost();
				int port=locator.getPort();
				String key=qs[1];
				
				socket = new Socket(hostname, port);
				
				out=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				
				
				out.writeUTF(NamedEntry.gRS2Store.toString());
				out.writeUTF(key);
				out.flush();
				
				in=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				URI newLocator =new URI(in.readUTF());
				this.fromLocator(newLocator);
				
			}
		} catch (UnknownHostException e)
		{
			if(in!=null) try{in.close();} catch(Exception ex){}
			if(out!=null) try{out.close();} catch(Exception ex){}
			if(socket!=null) try{socket.close();} catch(Exception ex){}
			throw new GRS2ProxyInvalidOperationException("Could not initialize new tcp locator", e);
		} catch (IOException e)
		{
			if(in!=null) try{in.close();} catch(Exception ex){}
			if(out!=null) try{out.close();} catch(Exception ex){}
			if(socket!=null) try{socket.close();} catch(Exception ex){}
			throw new GRS2ProxyInvalidOperationException("Could not initialize new tcp locator", e);
		} catch (URISyntaxException e)
		{
			if(in!=null) try{in.close();} catch(Exception ex){}
			if(out!=null) try{out.close();} catch(Exception ex){}
			if(socket!=null) try{socket.close();} catch(Exception ex){}
			throw new GRS2ProxyInvalidOperationException("Could not initialize new tcp locator", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method should only be used after the initialization step of the {@link TCPStoreReaderProxy#fromLocator(URI)}
	 * has been completed. After this is done, any call to this method simple forwards the request to he underlying
	 * {@link TCPReaderProxy#getBuffer()} method
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if a call to this overriding method is done even after the
	 * the store has been contacted and the underlying {@link TCPReaderProxy} needs to be used.
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.tcp.TCPReaderProxy#getBuffer()
	 */
	public IBuffer getBuffer() throws GRS2ProxyInvalidOperationException
	{
		if(!this.storeContacted) throw new GRS2ProxyInvalidOperationException("Method not supported for this proxy type");
		return super.getBuffer();
	}

	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>grs2-store-proxy://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#TCPStore</code>
	 * 
	 * @param locator the locator URI that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyStoreScheme.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.TCPStore.toString()))return false;
		return true;
	}
}
