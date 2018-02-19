package gr.uoa.di.madgik.grs.proxy.tcp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.lang.Thread.State;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorInvalidOperationException;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.proxy.tcp.mirror.TCPWriterMirror;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

/**
 * A connection handler that can be registered with the {@link TCPConnectionManager} in order to 
 * receive incoming TCP connection from clients. This entry is set to serve connections for the 
 * gRS2 component
 * 
 * @author gpapanikos
 *
 */
public class TCPConnectionHandler implements ITCPConnectionManagerEntry
{
	private static Logger logger = Logger.getLogger(TCPConnectionHandler.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The connection manager entry this handler can serve. The entry returned is marked
	 * with {@link gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry.NamedEntry#gRS2}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry#GetName()
	 */
	public NamedEntry GetName()
	{
		return NamedEntry.gRS2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * After a request of the correct type has reached {@link TCPConnectionManager}, it is forwarded here
	 * where the specific {@link IBuffer} and its serving {@link IMirror} is located. The request is
	 * forwarded to the {@link IMirror} and the operation is completed in this thread of execution. In
	 * the {@link IMirror} thread of execution, the mirroring protocol is initiated and served.<br/>
	 * The {@link IMirror} that is expected by this handler is one that manages TCP connections, the 
	 * {@link TCPWriterMirror} over which the {@link TCPWriterMirror#setSocket(Socket)} and
	 * {@link TCPWriterMirror#setKey(String)} is set before a call to {@link TCPWriterMirror#handle()}
	 * is invoked.<br/>
	 * If the state of the {@link TCPWriterMirror} thread is not {@link State#NEW}m then the connection
	 * is discarded as there is already a mirroring process in progress
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry#HandleConnection(java.net.Socket)
	 */
	@Override
	public void HandleConnection(Socket socket) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception e) {
			logger.log(Level.WARNING,"Could not open object input stream from socket",e);
		}
		boolean success=false;
		String key = null;
		try
		{
			key = ois.readUTF();
			IBuffer buffer=GRSRegistry.Registry.getBuffer(key);
			if(buffer!=null)
			{
				IMirror mirror=buffer.getMirror();
				if(mirror!=null && (mirror instanceof TCPWriterMirror))
				{
					if(((TCPWriterMirror)mirror).getState()!=State.NEW) throw new GRS2ProxyMirrorInvalidOperationException("Mirroring already initiated");
					((TCPWriterMirror)mirror).setSocket(socket);
					((TCPWriterMirror)mirror).setKey(key);
					((TCPWriterMirror)mirror).setInputStream(ois);
					((TCPWriterMirror)mirror).handle();
					success=true;
				}
			}
		}catch(Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Could not handle incoming mirroring request for key "+key,ex);
			success=false;
		}
		if(!success) try{ socket.close(); }catch(Exception ex){}
	}

}
