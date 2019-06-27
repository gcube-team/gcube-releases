package gr.uoa.di.madgik.grs.proxy.http;

import gr.uoa.di.madgik.commons.server.http.IHTTPConnectionManagerEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreReader;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.io.BufferedOutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A connection handler that can be registered with the {@link HTTPConnectionManager} in order to 
 * receive incoming HTTP connection from clients. This entry is set to serve connections for the 
 * gRS2 Store component
 * 
 * @author Alex Antoniadis
 *
 */
public class HTTPStoreConnectionHandler implements IHTTPConnectionManagerEntry
{
	
	private static Logger logger = Logger.getLogger(HTTPStoreConnectionHandler.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The connection manager entry this handler can serve. The entry returned is marked
	 * with {@link NamedEntry#gRS2Store}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.IHTTPConnectionManagerEntry#GetName()
	 */
	public NamedEntry GetName()
	{
		return NamedEntry.gRS2Store;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * After a request of the correct type has reached {@link HTTPConnectionManager}, it is forwarded here
	 * where the specific {@link IBufferStore} is located. A new {@link BufferStoreReader} is created
	 * targeting the requesting {@link IBufferStore}. A new {@link HTTPWriterProxy} instance is utilized by the
	 * reader to create a new URI locator that is then send back to the requester to user and access the reader's
	 * {@link IBuffer}. The populating thread of execution is managed by the {@link BufferStoreReader}.
	 * The connection received is then closed as the actual mirroring procedure will then start after a request
	 * that will be handled by the {@link HTTPConnectionHandler}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.IHTTPConnectionManagerEntry#HandleConnection(java.net.Socket)
	 */


	@Override
	public void HandleConnection(Socket socket, String request, BufferedOutputStream out, String key) {
		try
		{
			IBufferStore store=GRSRegistry.Registry.getStore(key);
			if(store!=null)
			{
				HTTPWriterProxy newProxy=new HTTPWriterProxy();
				BufferStoreReader storeReader=new BufferStoreReader(key, newProxy);
				URI storeAccessLocator=storeReader.populate();
				
				logger.log(Level.WARNING,"StoreAccessLocator : " + storeAccessLocator);
				
				out.write(storeAccessLocator.toString().getBytes());
				out.flush();
			}
		}catch(Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Could not handle incoming mirroring request for key "+key,ex);
		}
		try{ socket.close(); }catch(Exception ex){}
	}






}
