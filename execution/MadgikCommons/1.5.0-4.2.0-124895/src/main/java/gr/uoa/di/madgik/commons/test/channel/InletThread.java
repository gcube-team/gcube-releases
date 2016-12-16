package gr.uoa.di.madgik.commons.test.channel;

import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.events.StringPayloadChannelEvent;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InletThread extends Thread implements Observer
{
	private static Logger logger=Logger.getLogger(InletThread.class.getName()); 
	private ChannelInlet Inlet=null;
	private int NumberOfEventsToProduce=0;
	private final Object synchThreadStart=new Object();
	
	public InletThread(ChannelInlet Inlet,int NumberOfEventsToProduce)
	{
		this.Inlet=Inlet;
		this.NumberOfEventsToProduce=NumberOfEventsToProduce;
		this.setName(InletThread.class.getName());
		this.setDaemon(true);
		synchronized (this.synchThreadStart)
		{
			this.start();
			try{this.synchThreadStart.wait();}catch(Exception ex){}
		}
		for(ChannelStateEvent ev : this.Inlet.GetNozzleEvents())
		{
			ev.addObserver(this);
		}
	}
	
	@Override
	public void run()
	{
		synchronized(this.synchThreadStart)
		{
			this.synchThreadStart.notify();
		}
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Going to wait for a bit before starting producing");
		try{Thread.sleep(5000);}catch(Exception ex){}
		for(int i=0;i<NumberOfEventsToProduce;i+=1)
		{
			this.Inlet.Push(new StringPayloadChannelEvent("INLET(1) event "+i));
		}
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Done producing. going to wait for a bit");
		try{Thread.sleep(5000);}catch(Exception ex){}
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Done waiting. going to dispose");
		this.Inlet.Dispose();
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Done disposing. going to wait for a bit");
		try{Thread.sleep(5000);}catch(Exception ex){}
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Done waiting. Exiting");
	}

	public void update(Observable o, Object arg)
	{
		if (!(o.getClass().getName().equals(arg.getClass().getName())))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Caught event has argument other than the one registered for. Disgarding");
			return;
		}
		if(!(arg instanceof ChannelStateEvent)) return;
		if(arg instanceof StringPayloadChannelEvent)
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Inlet received "+((ChannelStateEvent)arg).GetEventName()+" with payload '"+((StringPayloadChannelEvent)arg).GetValue()+"'");
		}
		else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Inlet received "+((ChannelStateEvent)arg).GetEventName());
		}
	}
}
