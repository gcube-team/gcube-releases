package gr.uoa.di.madgik.commons.test.channel;

import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.events.StringPayloadChannelEvent;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutletThread implements Observer
{
	private static Logger logger=Logger.getLogger(InletThread.class.getName());
	private ChannelOutlet Outlet=null;
	private int OutletNumber=0;
	private Random gen=null;

	public OutletThread(ChannelOutlet Outlet,int OutletNumber)
	{
		this.Outlet=Outlet;
		this.OutletNumber=OutletNumber;
		this.gen=new Random(OutletNumber);
		for(ChannelStateEvent ev : this.Outlet.GetNozzleEvents())
		{
			ev.addObserver(this);
		}
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
			//if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Outlet("+this.OutletNumber+") received "+((ChannelStateEvent)arg).GetEventName());
			if(gen.nextInt(5)==0)
			{
				this.Outlet.Push(new StringPayloadChannelEvent(((StringPayloadChannelEvent)arg).GetValue()+" back from Outlet("+this.OutletNumber+")"));
			}
		}
		else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Outlet("+this.OutletNumber+") received "+((ChannelStateEvent)arg).GetEventName());
		}
	}
}
