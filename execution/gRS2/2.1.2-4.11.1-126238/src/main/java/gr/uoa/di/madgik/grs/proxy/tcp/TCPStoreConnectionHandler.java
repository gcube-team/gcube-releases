package gr.uoa.di.madgik.grs.proxy.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry.NamedEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreReader;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

/**
 * A connection handler that can be registered with the {@link TCPConnectionManager} in order to 
 * receive incoming TCP connection from clients. This entry is set to serve connections for the 
 * gRS2 Store component
 * 
 * @author gpapanikos
 *
 */
public class TCPStoreConnectionHandler implements ITCPConnectionManagerEntry
{
	private static Logger logger = Logger.getLogger(TCPStoreConnectionHandler.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The connection manager entry this handler can serve. The entry returned is marked
	 * with {@link NamedEntry#gRS2Store}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry#GetName()
	 */
	public NamedEntry GetName()
	{
		return NamedEntry.gRS2Store;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * After a request of the correct type has reached {@link TCPConnectionManager}, it is forwarded here
	 * where the specific {@link IBufferStore} is located. A new {@link BufferStoreReader} is created
	 * targeting the requesting {@link IBufferStore}. A new {@link TCPWriterProxy} instance is utilized by the
	 * reader to create a new URI locator that is then send back to the requester to user and access the reader's
	 * {@link IBuffer}. The populating thread of execution is managed by the {@link BufferStoreReader}.
	 * The connection received is then closed as the actual mirroring procedure will then start after a request
	 * that will be handled by the {@link TCPConnectionHandler}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry#HandleConnection(java.net.Socket)
	 */
	public void HandleConnection(ObjectInputStream ois, Socket socket)
	{
		String key = null;
		
		ObjectOutputStream oos = null;
//		DataOutputStream out=null;
//		DataInputStream in=null;
		try
		{
//			in=new DataInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			key = ois.readUTF();
			IBufferStore store=GRSRegistry.Registry.getStore(key);
			if(store!=null)
			{
				TCPWriterProxy newProxy=new TCPWriterProxy();
				BufferStoreReader storeReader=new BufferStoreReader(key, newProxy);
				URI storeAccessLocator=storeReader.populate();
				oos.writeUTF(storeAccessLocator.toString());
				oos.flush();
			}
		}catch(Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Could not handle incoming mirroring request for key "+key,ex);
		}
		try{ oos.flush(); }catch(Exception ex){}
		try{ oos.close(); }catch(Exception ex){}
		try{ ois.close(); }catch(Exception ex){}
		try{ socket.close(); }catch(Exception ex){}
	}

	@Override
	public void HandleConnection(Socket socket) {
		// TODO Auto-generated method stub
		
	}

}
