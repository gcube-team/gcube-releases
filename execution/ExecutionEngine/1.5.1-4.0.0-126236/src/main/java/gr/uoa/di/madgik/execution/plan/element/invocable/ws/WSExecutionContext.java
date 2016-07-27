package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.channel.events.ObjectPayloadChannelEvent;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.plan.element.invocable.IExecutionContext;
import gr.uoa.di.madgik.execution.plan.element.invocable.NozzleEventPayload;
import gr.uoa.di.madgik.execution.plan.element.invocable.NozzleHandler;
import gr.uoa.di.madgik.grs.proxy.IProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSExecutionContext implements IExecutionContext
{
	private static Logger logger=LoggerFactory.getLogger(WSExecutionContext.class);
	private String ContextID;
	private IChannelLocator Locator;
	private WSExecutionContextConfig Config;
	private String ExternalSender=null;
	
	private IProxy Proxy=null;
	private NozzleHandler Handler=null;
	
	public WSExecutionContext(String ContextID,String ExternalSender,IChannelLocator Locator,WSExecutionContextConfig Config) throws ExecutionInternalErrorException
	{
		this.ContextID=ContextID;
		this.Locator=Locator;
		this.Config=Config;
		this.ExternalSender=ExternalSender;
		if(this.Locator!=null)
		{
			this.Handler=new NozzleHandler();
			this.Handler.CreateOutletNozzle(Locator);
		}
	} 

	public IProxy GetProxy()
	{
		if(this.Proxy==null && this.Config!=null)
		{
			this.Proxy=this.Config.GetProxy();
		}
		return Proxy;
	}

	public void Report(String Message)
	{
		try
		{
			logger.debug("trying to send report");
			if(this.Handler==null) throw new Exception("No context handler set");
			logger.debug("sending report");
			this.Handler.Emitt(new ObjectPayloadChannelEvent(new NozzleEventPayload(new ExecutionExternalProgressReportStateEvent(this.ContextID, this.ExternalSender, Message))));
			logger.debug("send report");
		}catch(Exception ex)
		{
			logger.warn("Could not emit message",ex);
		}
	}

	public void Report(int CurrentStep, int TotalSteps)
	{
		try
		{
			logger.debug("trying to send report");
			if(this.Handler==null) throw new Exception("No context handler set");
			logger.debug("sending report");
			this.Handler.Emitt(new ObjectPayloadChannelEvent(new NozzleEventPayload(new ExecutionExternalProgressReportStateEvent(this.ContextID, this.ExternalSender, CurrentStep,TotalSteps))));
			logger.debug("send report");
		}catch(Exception ex)
		{
			logger.warn("Could not emit message",ex);
		}
	}

	public void Report(int CurrentStep, int TotalSteps, String Message)
	{
		try
		{
			logger.debug("trying to send report");
			if(this.Handler==null) throw new Exception("No context handler set");
			logger.debug("sending report");
			this.Handler.Emitt(new ObjectPayloadChannelEvent(new NozzleEventPayload(new ExecutionExternalProgressReportStateEvent(this.ContextID, this.ExternalSender, CurrentStep,TotalSteps,Message))));
			logger.debug("send report");
		}catch(Exception ex)
		{
			logger.warn("Could not emit message",ex);
		}
	}

	public void Close()
	{
		if(this.Handler!=null)
		{
			try
			{
				this.Handler.Dispose();
			} catch (ExecutionInternalErrorException ex)
			{
				logger.debug("Problem disposing Handler",ex);
			}
		}
	}

}
