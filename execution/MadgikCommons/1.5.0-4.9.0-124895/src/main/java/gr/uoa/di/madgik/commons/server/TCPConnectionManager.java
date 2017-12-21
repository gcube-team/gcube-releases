package gr.uoa.di.madgik.commons.server;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO fix denial of service
public class TCPConnectionManager extends ConnectionManager {
	protected static Logger logger = Logger
			.getLogger(TCPConnectionManager.class.getName());

	private static TCPConnectionManager Manager = null;
	protected static final Map<ITCPConnectionManagerEntry.NamedEntry, ITCPConnectionManagerEntry> Entries = new Hashtable<ITCPConnectionManagerEntry.NamedEntry, ITCPConnectionManagerEntry>();
	private static final Object lockInit = new Object();
	protected static final Object lockEntries = new Object();
	protected static final Object synchStart = new Object();

	protected ServerSocket Sock = null;

	public static void Init(TCPConnectionManagerConfig Config) {
		Init((ConnectionManagerConfig) Config);
	}

	public static void Init(ConnectionManagerConfig Config) {
		ConnectionManager.Config = Config;
		synchronized (TCPConnectionManager.lockInit) {
			if (TCPConnectionManager.Manager == null) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE,
							"Creating new instance of the connection manager");
				TCPConnectionManager.Manager = new TCPConnectionManager(
						TCPConnectionManager.OpenSocket());
			}
		}
	}

	public static void Init() throws FileNotFoundException, IOException {
		initializeConfigFromFile();

		synchronized (TCPConnectionManager.lockInit) {
			if (TCPConnectionManager.Manager == null) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE,
							"Creating new instance of the connection manager");
				TCPConnectionManager.Manager = new TCPConnectionManager(
						TCPConnectionManager.OpenSocket());
			}
		}
	}

	public static boolean IsInitialized() {
		synchronized (TCPConnectionManager.lockInit) {
			if (TCPConnectionManager.Manager == null)
				return false;
			else
				return true;
		}
	}

	public static String GetConnectionManagerHostName() {
		synchronized (TCPConnectionManager.lockInit) {
			if (TCPConnectionManager.Manager != null) {
				if (TCPConnectionManager.Config.HostName != null)
					return TCPConnectionManager.Config.HostName;
				return TCPConnectionManager.Manager.Sock.getInetAddress()
						.getHostAddress();
			}
			return null;
		}
	}

	public static int GetConnectionManagerPort() {
		synchronized (TCPConnectionManager.lockInit) {
			if (TCPConnectionManager.Manager != null) {
				return TCPConnectionManager.Manager.Sock.getLocalPort();
			}
			return -1;
		}
	}

	public static ITCPConnectionManagerEntry RegisterEntry(
			ITCPConnectionManagerEntry Entry) {
		logger.log(Level.WARNING, "Registering entry for entry name "
				+ Entry.GetName().toString());
		synchronized (TCPConnectionManager.lockEntries) {
			if (!TCPConnectionManager.Entries.containsKey(Entry.GetName())) {
				TCPConnectionManager.Entries.put(Entry.GetName(), Entry);
			}
			return TCPConnectionManager.Entries.get(Entry.GetName());
		}
	}

	public static ITCPConnectionManagerEntry GetEntry(
			ITCPConnectionManagerEntry.NamedEntry Name) {
		synchronized (TCPConnectionManager.lockEntries) {
			if (!TCPConnectionManager.Entries.containsKey(Name)) {
				logger.log(Level.WARNING, "Entry name " + Name
						+ " NOT found in Entries : "
						+ TCPConnectionManager.Entries);
				return null;
			} else {
				logger.log(Level.WARNING, "Entry name " + Name
						+ " found in Entries : " + TCPConnectionManager.Entries);
			}
			return TCPConnectionManager.Entries.get(Name);
		}
	}

	private TCPConnectionManager(ServerSocket Sock) {
		this.Sock = Sock;
		this.setName(TCPConnectionManager.class.getName());
		this.setDaemon(true);
		synchronized (TCPConnectionManager.synchStart) {
			this.start();
			try {
				TCPConnectionManager.synchStart.wait();
			} catch (Exception ex) {
			}
		}
	}

	public static ServerSocket OpenSocket() {
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Opening socket");
		for (PortRange range : TCPConnectionManager.Config.Ports) {
			for (int p = range.GetStart(); p <= range.GetEnd(); p += 1) {
				try {
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE,
								"Trying to open server socket on port " + p);
					ServerSocket servSocktmp = new ServerSocket(p);
					return servSocktmp;
				} catch (Exception ex) {
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE,
								"Could not open server socket on port " + p, ex);
				}
			}
		}
		if (TCPConnectionManager.Config.UseRandomIfNoneAvailable) {
			try {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE,
							"Trying to open server socket on random port");
				ServerSocket servSocktmp = new ServerSocket(0);
				return servSocktmp;
			} catch (Exception ex) {
				if (logger.isLoggable(Level.FINE))
					logger.log(Level.FINE,
							"Could not open server socket on random port ", ex);
			}
		}
		if (logger.isLoggable(Level.WARNING))
			logger.log(Level.WARNING,
					"Socket for proxy cannot be opened. No usable port could be found and bound to");
		throw new IllegalArgumentException(
				"Socket for proxy cannot be opened. No usable port could be found and bound to");
	}

	@Override
	public void run() {
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE,
					"Connection manager initialized listening to port ("
							+ this.Sock.getLocalPort() + ")");
		synchronized (TCPConnectionManager.synchStart) {
			TCPConnectionManager.synchStart.notify();
		}

		while (true) {
			Socket consumerSock = null;
			try {
				consumerSock = this.Sock.accept();
				logger.log(Level.FINE, " Received request from : "
						+ consumerSock.getInetAddress().getHostAddress());
				consumerSock.setSoTimeout(ConnectionManager.Config.timeout);
				final Socket newConsumerSock = consumerSock;
				new Thread(new Runnable() {
					@Override
					public void run() {

						// System.out.println("Before");
						// System.out.println("TCPConnectionManager Socket send buffer size    : "
						// + consumerSock.getSendBufferSize());
						// System.out.println("TCPConnectionManager Socket receive buffer size : "
						// + consumerSock.getReceiveBufferSize());
						//
						// consumerSock.setSendBufferSize(BUFFERSIZE);
						// consumerSock.setReceiveBufferSize(BUFFERSIZE);
						//
						// System.out.println("After");
						// System.out.println("TCPConnectionManager Socket send buffer size    : "
						// + consumerSock.getSendBufferSize());
						// System.out.println("TCPConnectionManager Socket receive buffer size : "
						// + consumerSock.getReceiveBufferSize());

						try {
							DataInputStream dis = null;
							InputStream is = newConsumerSock.getInputStream();

							String EntryNameString = null;
							dis = new DataInputStream(is);
							EntryNameString = dis.readUTF();

							ITCPConnectionManagerEntry.NamedEntry EntryName = ITCPConnectionManagerEntry.NamedEntry
									.valueOf(EntryNameString);
							ITCPConnectionManagerEntry Entry = TCPConnectionManager
									.GetEntry(EntryName);

							if (Entry == null) {
								if (logger.isLoggable(Level.WARNING))
									logger.log(Level.WARNING,
											"No handler set to forward connection for "
													+ EntryName.toString());
								newConsumerSock.close();
							} else {
								if (logger.isLoggable(Level.FINE)) {
									logger.log(Level.FINE,
											"New Connection opened and passed to "
													+ EntryName.toString());
									logger.log(Level.FINE, "");
								}

								Entry.HandleConnection(newConsumerSock);
							}
						} catch (Exception e) {
							if (logger.isLoggable(Level.WARNING))
								logger.log(
										Level.WARNING,
										"Could not handle new connection request",
										e);
							if (newConsumerSock != null
									&& !newConsumerSock.isClosed())
								try {
									newConsumerSock.close();
								} catch (IOException e1) {
								}
						}
					}
				}).start();

			} catch (IOException e1) {
				if (logger.isLoggable(Level.WARNING))
					logger.log(Level.WARNING,
							"Could not handle new connection request", e1);
				try {
					if (consumerSock != null && !consumerSock.isClosed())
						consumerSock.close();
				} catch (IOException ex) {
				}
			}
		}
	}
}
