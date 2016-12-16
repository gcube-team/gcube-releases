package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.events.EventFactory;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the synchronization protocol for the outlet nozzle sides of a channel.
 * 
 * @author gpapanikos
 */
public class OutletProtocol extends Thread implements Observer, Serializable
{
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger = Logger.getLogger(OutletProtocol.class.getName());
	
	/** The synch thread start. */
	private Object synchThreadStart = null;
	
	/** The client sock. */
	private Socket clientSock = null;
	
	/** The Nozzle id. */
	private String NozzleID = null;
	
	/** The Channel id. */
	private String ChannelID = null;
	
	/** The State. */
	private ChannelState State = null;
	
	/** The synch events to send. */
	private final Object synchEventsToSend = new Object();
	
	/** The Events to send. */
	private List<ChannelStateEvent> EventsToSend = null;
	
	/** The has connected. */
	private Boolean hasConnected = false;
	
	/** The still connected. */
	private Boolean stillConnected = false;
	
	/** The Constant WaitPeriodinMilliseconds. */
	private static final long WaitPeriodinMilliseconds = 100; // 100milliseconds
	
	/** The In dispose. */
	private Boolean InDispose = false;
	private final Boolean synchInDispose=new Boolean(false);

	/**
	 * Instantiates a new outlet protocol. The instance is set as a daemon thread and is started
	 * 
	 * @param synchThreadStart the object the client should wait on until the thread is started and a notify is emitted
	 * @param clientSock the socket the protocol should write and read synchronization info
	 * @param NozzleID the id of the nozzle this protocol is acting on behalf of
	 * @param ChannelID the id of the channel the nozzle is interested in
	 * @param State the state where to emit incoming events and register for outgoing events
	 */
	public OutletProtocol(Object synchThreadStart, Socket clientSock, String NozzleID, String ChannelID, ChannelState State)
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Created Outlet protocol");
		this.synchThreadStart = synchThreadStart;
		this.clientSock = clientSock;
		this.NozzleID = NozzleID;
		this.ChannelID = ChannelID;
		this.State = State;
		this.setName(OutletProtocol.class.getName());
		this.setDaemon(true);
		this.start();
	}

	/**
	 * Disposes the protocol state and unregisters from events
	 */
	public void Dispose()
	{
		synchronized(this.synchInDispose)
		{
			this.InDispose = true;
		}
		try
		{
			for (ChannelStateEvent ev : this.State.GetChannelEvents())
			{
				ev.deleteObserver(this);
			}
		} catch (Exception ex)
		{
		}
		this.EventsToSend.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Outlet protocol running");
		try
		{
			this.hasConnected = true;
			this.stillConnected = true;
			this.EventsToSend = new ArrayList<ChannelStateEvent>();
			for (ChannelStateEvent ev : this.State.GetChannelEvents())
			{
				ev.addObserver(this);
			}
			synchronized (this.synchThreadStart)
			{
				this.synchThreadStart.notify();
			}
			DataInputStream din = new DataInputStream(this.clientSock.getInputStream());
			DataOutputStream dout = new DataOutputStream(this.clientSock.getOutputStream());
			dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.Channel.toString());
			dout.writeUTF(this.NozzleID);
			dout.writeUTF(this.ChannelID);
			while (true)
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Iterating");
				synchronized(this.synchInDispose)
				{
					if (this.InDispose) break;
				}
				this.EmitOutgoingEvents(dout);
				this.ReceiveIncomingEvents(din);
				synchronized (this.synchEventsToSend)
				{
					if (this.EventsToSend.size() == 0)
					{
						try
						{
							this.synchEventsToSend.wait(OutletProtocol.WaitPeriodinMilliseconds);
						} catch (Exception ex)
						{
						}
					}
				}
			}
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Break Iteration");
		} catch (Exception ex)
		{
			this.stillConnected = false;
			if(!this.InDispose) if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Outlet protocol thread could not complete normally", ex);
		}
	}

	/**
	 * Checks if the outlet is connected to the inlet channel
	 * 
	 * @return whether or not the outlet is connected to the inlet channel
	 */
	public Boolean HasConnected()
	{
		return this.hasConnected;
	}

	/**
	 * Checks if the outlet is still connected to the inlet channel
	 * 
	 * @return whether or not the outlet is still connected to the inlet channel
	 */
	public Boolean StillConnected()
	{
		return this.stillConnected;
	}

	/**
	 * Writes to the outgoing stream the events that have been emitted from the {@link ChannelOutlet}
	 * and that are produced by this nozzle
	 * 
	 * @param dout the outgoing stream
	 * @throws Exception the operation could not be completed
	 */
	private void EmitOutgoingEvents(DataOutputStream dout) throws Exception
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Emiting Outgoing events");
		List<ChannelStateEvent> TmpEvents = new ArrayList<ChannelStateEvent>();
		synchronized (this.synchEventsToSend)
		{
			for(ChannelStateEvent ev : this.EventsToSend)
			{
				if(ev.GetEmitingNozzleID().equals(this.NozzleID)) TmpEvents.add(ev);
			}
			this.EventsToSend.clear();
		}
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Number of Outgoing events is "+TmpEvents.size());
		dout.writeInt(TmpEvents.size());
		dout.flush();
		for (ChannelStateEvent ev : TmpEvents)
		{
			byte[] evbuf = null;
			try
			{
				evbuf = ev.Encode();
			}catch(Exception ex)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not serialize event "+ev+". Disgarding",ex);
				continue;
			}
			dout.writeUTF(ev.GetEventName().toString());
			dout.writeInt(evbuf.length);
			dout.write(evbuf);
		}
		dout.flush();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Finished Emiting Outgoing events");
	}

	/**
	 * Reads from the incoming stream the send events and forwards them to the {@link ChannelOutlet}.
	 * The only events that are forwarded are the ones that were not send by this nozzle
	 * 
	 * @param din the incoming stream
	 * @throws Exception the operation could not be completed
	 */
	private void ReceiveIncomingEvents(DataInputStream din) throws Exception
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Receiving Incoming events");
		int incoming = din.readInt();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Number of Incoming events is "+incoming);
		for (int i = 0; i < incoming; i += 1)
		{
			ChannelState.EventName evtype = ChannelState.EventName.valueOf(din.readUTF());
			int size = din.readInt();
			byte[] evbuf = new byte[size];
			din.readFully(evbuf);
			ChannelStateEvent ev = null;
			try
			{
				ev = EventFactory.GetEvent(evtype, evbuf);
			}catch(Exception ex)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Problem deserializing incoming event. Disgarding",ex);
				continue;
			}
			if (ev.GetEmitingNozzleID().equals(this.NozzleID)) continue;
			ChannelStateEvent evv = this.State.GetEvent(ev.GetEventName());
			if (evv != null)
			{
				evv.NotifyChange(ev);
			}
		}
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Finished receiving Incoming events");
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (!(o.getClass().getName().equals(arg.getClass().getName())))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Caught event has argument other than the one registered for. Disgarding");
			return;
		}
		if (arg instanceof ChannelStateEvent)
		{
			synchronized (this.synchEventsToSend)
			{
				this.EventsToSend.add((ChannelStateEvent) arg);
			}
		}
	}
}
