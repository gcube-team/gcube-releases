package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.commons.channel.events.ChannelPayloadStateEvent;
import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.events.ObjectPayloadChannelEvent;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NozzleHandler implements Observer
{
	private static Logger logger=LoggerFactory.getLogger(NozzleHandler.class);
	private ChannelInlet inlet=null;
	private ChannelOutlet outlet=null;
	private ExecutionHandle Handle=null;
	
	public NozzleHandler(){}
	
	public IChannelLocator CreateInletNozzle(INozzleConfig Config,ExecutionHandle Handle) throws ExecutionInternalErrorException
	{
		this.inlet=new ChannelInlet(Config);
		this.Handle=Handle;
		this.RegisterForEvents();
		return this.inlet.GetLocator();
	}
	
	public void CreateOutletNozzle(IChannelLocator Locator) throws ExecutionInternalErrorException
	{
		this.outlet=new ChannelOutlet(Locator);
		this.RegisterForEvents();
	}
	
	public void Emitt(ChannelPayloadStateEvent Event) throws ExecutionInternalErrorException
	{
		logger.debug("Emiting event from Nozzle Handle");
		if(this.inlet!=null)
		{
			logger.debug("Emiting event from inlet");
			this.inlet.Push(Event);
		}
		else if (this.outlet!=null)
		{
			logger.debug("Emiting event from outlet");
			this.outlet.Push(Event);
		}
		else throw new ExecutionInternalErrorException("No nozzle is initialized");
	}
	
	public void Dispose() throws ExecutionInternalErrorException
	{
		try
		{
			for(ChannelStateEvent ev : this.GetPublishedEvents())
			{
				ev.deleteObserver(this);
			}
			if(this.inlet!=null) this.inlet.Dispose();
			else if(this.outlet!=null) this.outlet.Dispose();
			else throw new ExecutionInternalErrorException("No nozzle is initialized");
		}catch(Exception ex)
		{
			logger.error("Problem disposing Nozzle Handler",ex);
		}
	}

	public void update(Observable o, Object arg)
	{
		try
		{
			if(!o.getClass().getName().equals(arg.getClass().getName()))
			{
				logger.debug("Received invalid event");
				return;
			}
			if(!(arg instanceof ChannelStateEvent))
			{
				logger.debug("Received invalid event");
				return;
			}
			switch(((ChannelStateEvent)arg).GetEventName())
			{
				case ObjectPayload:
				{
					logger.debug("Received object payload event");
					if(this.Handle==null && this.inlet!=null) throw new ExecutionInternalErrorException("In inlet nozzle side no execution handle set to forward event");
					else if(this.Handle==null && this.outlet!=null) return;
					if(!(arg instanceof ObjectPayloadChannelEvent)) throw new ExecutionInternalErrorException("Reveived object payload event of unrecognized type");
					if(!(((ObjectPayloadChannelEvent)arg).GetValue() instanceof NozzleEventPayload)) throw new ExecutionInternalErrorException("Reveived object payload event of unrecognized payload type");
					if(((NozzleEventPayload)(((ObjectPayloadChannelEvent)arg).GetValue())).ExecutionEngineEvent==null)  throw new ExecutionInternalErrorException("Received nozzle event with no execution engine event set");
					this.Handle.EmitEvent(((NozzleEventPayload)(((ObjectPayloadChannelEvent)arg).GetValue())).ExecutionEngineEvent);
					break;
				}case DisposeChannel:
				{
					logger.debug("Received dispose channel event");
					this.Dispose();
					break;
				}
				case StringPayload:
				case BytePayload:
				default:
				{
					logger.warn("Received event "+((ChannelStateEvent)arg).GetEventName()+" which was not expected");
					break;
				}
			}
		}catch(Exception ex)
		{
			logger.warn("Could not process event",ex);
		}
	}
	
	private void RegisterForEvents() throws ExecutionInternalErrorException
	{
		for(ChannelStateEvent ev : this.GetPublishedEvents())
		{
			ev.addObserver(this);
		}
	}
	
	private Collection<ChannelStateEvent> GetPublishedEvents() throws ExecutionInternalErrorException
	{
		Collection<ChannelStateEvent> events=null;
		if(this.inlet!=null) events=this.inlet.GetNozzleEvents();
		else if(this.outlet!=null) events=this.outlet.GetNozzleEvents();
		else throw new ExecutionInternalErrorException("No nozzle is initialized");
		return events;
	}

}
