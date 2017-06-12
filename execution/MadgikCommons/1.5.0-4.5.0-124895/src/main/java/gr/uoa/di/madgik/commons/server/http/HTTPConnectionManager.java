package gr.uoa.di.madgik.commons.server.http;

import gr.uoa.di.madgik.commons.server.ConnectionManager;
import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public class HTTPConnectionManager extends ConnectionManager {
	private static Logger logger = Logger.getLogger(HTTPConnectionManager.class.getName());

	private static HTTPConnectionManager Manager = null;
	private static final Map<IHTTPConnectionManagerEntry.NamedEntry, IHTTPConnectionManagerEntry> Entries = new Hashtable<IHTTPConnectionManagerEntry.NamedEntry, IHTTPConnectionManagerEntry>();
	private static final Object lockInit = new Object();
	private static final Object lockEntries = new Object();
	private static final Object synchStart = new Object();
//	private static HTTPConnectionManagerConfig Config = null;

	private ServerSocket Sock = null;

	public static void Init(ConnectionManagerConfig Config) {
		HTTPConnectionManager.Config = Config;
		synchronized (HTTPConnectionManager.lockInit) {
			if (HTTPConnectionManager.Manager == null) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE, "Creating new instance of the connection manager");
				HTTPConnectionManager.Manager = new HTTPConnectionManager(HTTPConnectionManager.OpenSocket());
			}
		}
	}
	
	public static void Init() throws FileNotFoundException, IOException {
		initializeConfigFromFile();
		
		synchronized (HTTPConnectionManager.lockInit) {
			if (HTTPConnectionManager.Manager == null) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE, "Creating new instance of the connection manager");
				HTTPConnectionManager.Manager = new HTTPConnectionManager(HTTPConnectionManager.OpenSocket());
			}
		}
	}

	public static boolean IsInitialized() {
		synchronized (HTTPConnectionManager.lockInit) {
			if (HTTPConnectionManager.Manager == null)
				return false;
			else
				return true;
		}
	}

	public static String GetConnectionManagerHostName() {
		synchronized (HTTPConnectionManager.lockInit) {
			if (HTTPConnectionManager.Manager != null) {
				if (HTTPConnectionManager.Config.HostName != null)
					return HTTPConnectionManager.Config.HostName;
				return HTTPConnectionManager.Manager.Sock.getInetAddress().getHostAddress();
			}
			return null;
		}
	}

	public static int GetConnectionManagerPort() {
		synchronized (HTTPConnectionManager.lockInit) {
			if (HTTPConnectionManager.Manager != null) {
				return HTTPConnectionManager.Manager.Sock.getLocalPort();
			}
			return -1;
		}
	}

	public static IHTTPConnectionManagerEntry RegisterEntry(IHTTPConnectionManagerEntry Entry) {
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Registering entry for entry name " + Entry.GetName().toString());
		synchronized (HTTPConnectionManager.lockEntries) {
			if (!HTTPConnectionManager.Entries.containsKey(Entry.GetName())) {
				HTTPConnectionManager.Entries.put(Entry.GetName(), Entry);
			}
			return HTTPConnectionManager.Entries.get(Entry.GetName());
		}
	}

	public static IHTTPConnectionManagerEntry GetEntry(IHTTPConnectionManagerEntry.NamedEntry Name) {
		synchronized (HTTPConnectionManager.lockEntries) {
			if (!HTTPConnectionManager.Entries.containsKey(Name))
				return null;
			return HTTPConnectionManager.Entries.get(Name);
		}
	}

	private HTTPConnectionManager(ServerSocket Sock) {
		this.Sock = Sock;
		this.setName(HTTPConnectionManager.class.getName());
		this.setDaemon(true);
		synchronized (HTTPConnectionManager.synchStart) {
			this.start();
			try {
				HTTPConnectionManager.synchStart.wait();
			} catch (Exception ex) {
			}
		}
	}

	public static ServerSocket OpenSocket() {
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Opening socket");
		for (PortRange range : HTTPConnectionManager.Config.Ports) {
			for (int p = range.GetStart(); p <= range.GetEnd(); p += 1) {
				try {
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE, "Trying to open server socket on port " + p);
					ServerSocket servSocktmp = new ServerSocket(p);
					return servSocktmp;
				} catch (Exception ex) {
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE, "Could not open server socket on port " + p, ex);
				}
			}
		}
		if (HTTPConnectionManager.Config.UseRandomIfNoneAvailable) {
			try {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE, "Trying to open server socket on random port");
				ServerSocket servSocktmp = new ServerSocket(0);
				return servSocktmp;
			} catch (Exception ex) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE, "Could not open server socket on random port ", ex);
			}
		}
		if (logger.isLoggable(Level.WARNING))
			logger.log(Level.WARNING, "Socket for proxy cannot be opened. No usable port could be found and bound to");
		throw new IllegalArgumentException(
				"Socket for proxy cannot be opened. No usable port could be found and bound to");
	}

	@Override
	public synchronized void run() {
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Connection manager initialized listening to port (" + this.Sock.getLocalPort()
					+ ")");
		synchronized (HTTPConnectionManager.synchStart) {
			HTTPConnectionManager.synchStart.notify();
		}
		while (true) {
			Socket consumerSock = null;
			try {
				logger.log(Level.FINEST,"HTTPConnectionManager : Accepting..." );
				consumerSock = this.Sock.accept();
				logger.log(Level.FINEST,"HTTPConnectionManager : Accepted");
				logger.log(Level.FINEST,"HTTPConnectionManager : Socket host : " + consumerSock.getInetAddress().getHostAddress());
				logger.log(Level.FINEST,"HTTPConnectionManager : Socket port : " + consumerSock.getPort());
				logger.log(Level.FINEST,"HTTPConnectionManager : remote address : " + consumerSock.getRemoteSocketAddress().toString());
				

				BufferedReader netStream = new BufferedReader(new InputStreamReader(consumerSock.getInputStream()));

				StringBuffer strBuf = new StringBuffer();
				String key = null;
				String EntryNameString = null;
				Integer contentLength = -1;
				String line = null;
				
				
				while ((line = netStream.readLine()) != null) {
					String words[] = line.split(" ");
					if (words.length > 1 && (words[0].equalsIgnoreCase("Content-Length:"))) {
						contentLength = Integer.valueOf(words[1].trim());
					} else if (words.length > 1 && (words[0].equalsIgnoreCase("key:"))) {
						key = words[1].trim();
					} else if (words.length > 1 && (words[0].equalsIgnoreCase("EntryName:"))) {
						EntryNameString = words[1].trim();
					}

					if (line.trim().length() == 0)
						break;
					strBuf.append(line + "\n");
				}
//				System.out.println("Received : " + strBuf.toString());
				
				if (strBuf.toString().trim().length() == 0){
					logger.log(Level.FINEST, "Trying to parse it byte-byte");
					DataInputStream dis = new DataInputStream(consumerSock.getInputStream());
					while (dis.available() > 0){
						char c = dis.readChar();
						logger.log(Level.FINEST, String.valueOf(c));
					}
					
					
					throw new Exception("Empty request received");
				}
				
				StringBuffer request = new StringBuffer();
				while (contentLength-->0)
					request.append((char)netStream.read());

				if (key == null)
					throw new Exception("Key not defined");

//				logger.log(Level.WARNING, "HTTPConnectionManager XML request : " + request);
//				logger.log(Level.WARNING, "HTTPConnectionManager EntryNameString : " + EntryNameString);
//				logger.log(Level.WARNING, "HTTPConnectionManager Key : " + key);
				
				IHTTPConnectionManagerEntry.NamedEntry EntryName = IHTTPConnectionManagerEntry.NamedEntry
						.valueOf(EntryNameString);
				IHTTPConnectionManagerEntry Entry = HTTPConnectionManager.GetEntry(EntryName);

				if (Entry == null) {
					if (logger.isLoggable(Level.WARNING))
						logger.log(Level.WARNING, "No handler set to forward connection for " + EntryName.toString());
					consumerSock.close();
				} else {
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE, "New Connection opened and passed to " + EntryName.toString());
					Entry.HandleConnection(consumerSock, request.toString(),
							new BufferedOutputStream(consumerSock.getOutputStream()), key);
				}

			} catch (Exception ex) {
				if (logger.isLoggable(Level.WARNING))
					logger.log(Level.WARNING, "Could not handle new connection request", ex);
				try {
					consumerSock.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
