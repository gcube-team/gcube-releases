package gr.uoa.di.madgik.grs.proxy.http;

import gr.uoa.di.madgik.commons.server.http.HTTPConnectionManager;
import gr.uoa.di.madgik.commons.server.http.IHTTPConnectionManagerEntry;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.http.mirror.HTTPWriterMirror;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;

import java.io.BufferedOutputStream;
import java.lang.Thread.State;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A connection handler that can be registered with the
 * {@link HTTPConnectionManager} in order to receive incoming HTTP connection from
 * clients. This entry is set to serve connections for the gRS2 component
 * 
 * @author Alex Antoniadis
 * 
 */
public class HTTPConnectionHandler implements IHTTPConnectionManagerEntry {
	private static Logger logger = Logger.getLogger(HTTPConnectionHandler.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The connection manager entry this handler can serve. The entry returned
	 * is marked with
	 * {@link gr.uoa.di.madgik.commons.server.IHTTPConnectionManagerEntry.NamedEntry#gRS2}
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.IHTTPConnectionManagerEntry#GetName()
	 */
	public NamedEntry GetName() {
		return NamedEntry.gRS2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * After a request of the correct type has reached
	 * {@link HTTPConnectionManager}, it is forwarded here where the specific
	 * {@link IBuffer} and its serving {@link IMirror} is located. The request
	 * is forwarded to the {@link IMirror} and the operation is completed in
	 * this thread of execution. In the {@link IMirror} thread of execution, the
	 * mirroring protocol is initiated and served.<br/>
	 * The {@link IMirror} that is expected by this handler is one that manages
	 * HTTP connections, the {@link HTTPWriterMirror} over which the
	 * {@link HTTPWriterMirror#setSocket(Socket)} and
	 * {@link HTTPWriterMirror#setKey(String)} is set before a call to
	 * {@link HTTPWriterMirror#handle()} is invoked.<br/>
	 * If the state of the {@link HTTPWriterMirror} thread is not
	 * {@link State#NEW}m then the connection is discarded as there is already a
	 * mirroring process in progress
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.commons.server.IHTTPConnectionManagerEntry#HandleConnection(java.net.Socket)
	 */
	public void HandleConnection(Socket socket, String request, BufferedOutputStream out, String key) {
		
		boolean success=false;
		try {
			IBuffer buffer = GRSRegistry.Registry.getBuffer(key);
			if (buffer != null) {
				IMirror mirror = buffer.getMirror();
				if (mirror != null && (mirror instanceof HTTPWriterMirror)) {
					synchronized (mirror) {
						((HTTPWriterMirror) mirror).setKey(key);
						((HTTPWriterMirror) mirror).handle(request, socket, out);
						success=true;
					}
				}
			}
		} catch (Exception ex) {
			if (logger.isLoggable(Level.WARNING))
				logger.log(Level.WARNING, "Could not handle incoming mirroring request for key " + key, ex);
		}
		
		if(!success) try{ socket.close(); }catch(Exception ex){}
		
	}

}
