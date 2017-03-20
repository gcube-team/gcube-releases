package gr.uoa.di.madgik.grs.proxy.http;

import gr.uoa.di.madgik.commons.server.http.IHTTPConnectionManagerEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidArgumentException;
import gr.uoa.di.madgik.grs.proxy.GRS2ProxyInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * This proxy extends the {@link HTTPReaderProxy} capabilities by adding an additional logic layer
 * on top of the original extended base capabilities. This additional logic layer serves to contact the
 * {@link IBufferStore} that can then create a locator capable of being managed by the underlying
 * {@link HTTPReaderProxy} instance. 
 * 
 * @author Alex Antoniadis
 *
 */
public class HTTPStoreReaderProxy extends HTTPReaderProxy
{
	private static Logger logger = Logger.getLogger(HTTPStoreReaderProxy.class.getName());
	
	private boolean storeContacted=false;
	
	/**
	 * Creates a new instance
	 */
	public HTTPStoreReaderProxy()
	{
		this.storeContacted=false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method parses the provided URI locator that is expected to be in the form of the following example
	 * <code>http://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#HTTPStore</code>. After the
	 * parsing is completed, a connection is made to the provided host and port where it is expected that an instance
	 * of the {@link HTTPStoreConnectionHandler} is listening. The key of the {@link IBufferStore} is send and the URI 
	 * locator is retrieved which is then forwarded to the underlying {@link HTTPReaderProxy} to take over any subsequent actions 
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidArgumentException If the provided locator is not of the expected type of the parsing procedure
	 * does not provide the valid information needed
	 * @throws GRS2ProxyInvalidOperationException if there was a problem initializing the underlying {@link HTTPReaderProxy}
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.http.HTTPReaderProxy#fromLocator(java.net.URI)
	 */
	public void fromLocator(URI locator) throws GRS2ProxyInvalidArgumentException, GRS2ProxyInvalidOperationException
	{
		HttpURLConnection connection=null;
		BufferedReader  in=null;
		try
		{
			if(this.storeContacted)super.fromLocator(locator);
			else
			{
				this.storeContacted=true;
				if(locator==null) throw new GRS2ProxyInvalidArgumentException("Locator cannot be null");
				if(!HTTPStoreReaderProxy.isOfType(locator)) throw new GRS2ProxyInvalidArgumentException("Locator is not of appropriate type "+locator.toString());
				if(locator.getQuery()==null) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
				String []qs=locator.getQuery().trim().split("=");
				if(qs.length!=2) throw new GRS2ProxyInvalidArgumentException("Invalid query string in locator "+locator.toString());
				if(qs[1].trim().length()==0) throw new GRS2ProxyInvalidArgumentException("Invalid key in locator "+locator.toString());
				String hostname=locator.getHost();
				int port=locator.getPort();
				String key=qs[1];
				
				connection = (HttpURLConnection) new URL("http://" + hostname + ":" + port).openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("key", key);
				connection.setRequestProperty("EntryName", IHTTPConnectionManagerEntry.NamedEntry.gRS2Store.name());
				
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String newLocatorStr = in.readLine();
				URI newLocator =new URI(newLocatorStr);
				
				this.fromLocator(newLocator);
			}
		} catch (URISyntaxException e)
		{
			if(in!=null) try{in.close();} catch(Exception ex){}
			if(connection!=null) try{connection.disconnect();} catch(Exception ex){}
			throw new GRS2ProxyInvalidOperationException("Could not initialize new HTTP locator", e);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method should only be used after the initialization step of the {@link HTTPStoreReaderProxy#fromLocator(URI)}
	 * has been completed. After this is done, any call to this method simple forwards the request to he underlying
	 * {@link HTTPReaderProxy#getBuffer()} method
	 * </p>
	 * 
	 * @throws GRS2ProxyInvalidOperationException if a call to this overriding method is done even after the
	 * the store has been contacted and the underlying {@link HTTPReaderProxy} needs to be used.
	 * 
	 * @see gr.uoa.di.madgik.grs.proxy.http.HTTPReaderProxy#getBuffer()
	 */
	public IBuffer getBuffer() throws GRS2ProxyInvalidOperationException
	{
		if(!this.storeContacted) throw new GRS2ProxyInvalidOperationException("Method not supported for this proxy type");
		return super.getBuffer();
	}

	/**
	 * Checks if the provided URI is of a type that can be managed by an instance of this class.
	 * It bases its checks on the URI scheme and the fragment type. An example of the URI
	 * this proxy can manage is <code>http://foo.bar.gr:53361?key=272b934d-e796-4c30-944c-44ec2aec7eb7#HTTPStore</code>
	 * 
	 * @param locator the locator URI that needs to be checked
	 * @return true is this proxy class can handle the locator, false otherwise
	 */
	public static boolean isOfType(URI locator)
	{
		if(!locator.getScheme().equalsIgnoreCase(IProxy.ProxyType.HTTP.toString()))return false;
		if(!locator.getFragment().equalsIgnoreCase(IProxy.ProxyType.HTTPStore.toString()))return false;
		return true;
	}
}
