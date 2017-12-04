package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.events.EventFactory;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryEntry;
import gr.uoa.di.madgik.commons.channel.registry.RegisteredNozzle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the synchronization protocol for the inlet nozzle sides of a channel.
 * 
 * @author gpapanikos
 */
public class InletProtocol extends Thread implements Observer, Serializable
{
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger = Logger.getLogger(InletProtocol.class.getName());
	
	/** The Entry. */
	private ChannelRegistryEntry Entry = null;
	
	/** The synch thread start. */
	private Object synchThreadStart = null;
	
	/** The Events to send. */
	private List<ChannelStateEvent> EventsToSend = null;
	
	/** The synch events to send. */
	private final Boolean synchEventsToSend = new Boolean(false);
	
	/** The Constant MaximumWaitPeriodinMilliseconds. */
	private static final long MaximumWaitPeriodinMilliseconds = 1000 * 60 * 1; // 1 minute
	
	/** The Constant WaitPeriodinMilliseconds. */
	private static final long WaitPeriodinMilliseconds = 50; // 50 milliseconds
	
	/** The In dispose. */
	private Boolean InDispose=false;

	/**
	 * Instantiates a new inlet protocol. The instance is set as a daemon thread and is started
	 * 
	 * @param Entry The {@link ChannelRegistryEntry} that represents the channel that is served
	 * @param synchThreadStart the object the client should wait on until the thread is started and a notify is emitted
	 */
	public InletProtocol(ChannelRegistryEntry Entry, Object synchThreadStart)
	{
		this.Entry = Entry;
		this.synchThreadStart = synchThreadStart;
		this.setName(InletProtocol.class.getName());
		this.setDaemon(true);
		this.start();
	}

	/**
	 * Disposes the protocol state and unregisters from events
	 */
	public void Dispose()
	{
		this.InDispose=true;
		try
		{
			for (ChannelStateEvent ev : this.Entry.GetState().GetChannelEvents())
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
		this.EventsToSend = new ArrayList<ChannelStateEvent>();
		try
		{
			for (ChannelStateEvent ev : this.Entry.GetState().GetChannelEvents())
			{
				ev.addObserver(this);
			}
		} catch (Exception ex)
		{
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Inlet protocol thread could not complete normally", ex);
		}
		synchronized (this.synchThreadStart)
		{
			this.synchThreadStart.notify();
		}
		try
		{
			while (true)
			{
				if(this.InDispose) break;
				List<RegisteredNozzle> Nozzles = this.Entry.GetRegisteredNozzles();
				this.ReceiveIncomingEvents(Nozzles);
				this.EmitOutgoingEvents(Nozzles);
			}
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Break Iteration");
		} catch (Exception ex)
		{
			if(!this.InDispose) if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Inlet protocol thread could not complete normally", ex);
		}
	}

	/**
	 * Writes to the outgoing stream of each connected nozzle the events that have been emitted from 
	 * the {@link ChannelInlet} and that are produced by this nozzle
	 * 
	 * @param Nozzles the nozzles that should be notified for the outgoing events
	 */
	private void EmitOutgoingEvents(List<RegisteredNozzle> Nozzles)
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Emiting Outgoing events");
		List<ChannelStateEvent> TmpEvents = new ArrayList<ChannelStateEvent>();
		synchronized (this.synchEventsToSend)
		{
			TmpEvents.addAll(this.EventsToSend);
			this.EventsToSend.clear();
		}
		for (RegisteredNozzle nozzle : Nozzles)
		{
			try
			{
				if (nozzle.GetClientSock() == null) continue;
				DataOutputStream dout = new DataOutputStream(nozzle.GetClientSock().getOutputStream());
				int count=0;
				for (ChannelStateEvent ev : TmpEvents)
				{
					if(ev.GetEmitingNozzleID().equals(nozzle.GetNozzleID())) count+=1;
				}
				int evtosend=TmpEvents.size()-count;
				if(evtosend<0) evtosend=0; 
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Number of Outgoing events is "+evtosend);
				dout.writeInt(evtosend);
				dout.flush();
				for (ChannelStateEvent ev : TmpEvents)
				{
					if(ev.GetEmitingNozzleID().equals(nozzle.GetNozzleID())) continue;
					dout.writeUTF(ev.GetEventName().toString());
					byte[] evbuf = ev.Encode();
					dout.writeInt(evbuf.length);
					dout.write(evbuf);
				}
				dout.flush();
			} catch (Exception ex)
			{
				nozzle.Dispose();
			}
		}
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Finished Emiting Outgoing events");
	}

	/**
	 * Waits for all connected nozzles to be ready to send events. Then it reads from each of them and
	 * forwards the events to the {@link ChannelInlet}. The events that are forwarded are only the ones 
	 * that were not initially send from this nozzle
	 * 
	 * @param Nozzles the nozzles the connected nozzle
	 */
	private void ReceiveIncomingEvents(List<RegisteredNozzle> Nozzles)
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Receiving Incoming events");
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Waiting for everyone to be able to transmit");
		this.WaitForAll(Nozzles);
		if(this.InDispose) return;
		for (RegisteredNozzle nozzle : Nozzles)
		{
			try
			{
				if (nozzle.GetClientSock() == null) continue;
				DataInputStream din = new DataInputStream(nozzle.GetClientSock().getInputStream());
				if (din.available() <= 0) continue;
				int incoming = din.readInt();
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Number of Incoming events is "+incoming);
				for (int i = 0; i < incoming; i += 1)
				{
					ChannelState.EventName evtype = ChannelState.EventName.valueOf(din.readUTF());
					int size = din.readInt();
					if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "size of event is "+size);
					byte[] evbuf = new byte[size];
					din.readFully(evbuf);
					if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "read event is "+evbuf.length);
					ChannelStateEvent ev = EventFactory.GetEvent(evtype, evbuf);
					if (ev.GetEmitingNozzleID().equals(this.Entry.GetInletNozzleID())) continue;
					ChannelStateEvent evv = this.Entry.GetState().GetEvent(ev.GetEventName());
					if (evv != null)
					{
						if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Emiting received "+ev.GetEventName().toString());
						evv.NotifyChange(ev);
					}
				}
			} catch (Exception ex)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Problem receiving events from nozzle. Disposing nozzle",ex);
				nozzle.Dispose();
			}
		}
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Finished receiving Incoming events");
	}

	/**
	 * Wait for all nozzles that are provided to be ready to send their outgoing events.
	 * 
	 * @param Nozzles the nozzles that should be checked that are ready to send
	 */
	private void WaitForAll(List<RegisteredNozzle> Nozzles)
	{
		for (RegisteredNozzle nozzle : Nozzles)
		{
			if(this.InDispose) return;
			try
			{
				if (nozzle.GetClientSock() == null) continue;
				if (nozzle.GetClientSock().isClosed())
				{
					nozzle.Dispose();
					continue;
				}
				long sleepedSoFar = 0;
				while (true)
				{
					if(this.InDispose) return;
					if (sleepedSoFar > InletProtocol.MaximumWaitPeriodinMilliseconds)
					{
						if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing nozzle because of maximum amount reached");
						nozzle.Dispose();
						break;
					}
					if (new DataInputStream(nozzle.GetClientSock().getInputStream()).available() <= 0)
					{
						try
						{
							Thread.sleep(InletProtocol.WaitPeriodinMilliseconds);
							sleepedSoFar += InletProtocol.WaitPeriodinMilliseconds;
						} catch (Exception ex)
						{
						}
					}
					else break;
				}
			} catch (Exception ex)
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing nozzle because of error " + ex.getMessage());
				nozzle.Dispose();
			}
		}
		if(Nozzles.size()==0)
		try
		{
			Thread.sleep(InletProtocol.WaitPeriodinMilliseconds);
		} catch (Exception ex)
		{
		}
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
