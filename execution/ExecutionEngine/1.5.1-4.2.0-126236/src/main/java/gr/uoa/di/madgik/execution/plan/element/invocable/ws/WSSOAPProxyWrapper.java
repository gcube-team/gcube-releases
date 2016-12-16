package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.NozzleHandler;

import java.io.Serializable;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSSOAPProxyWrapper implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger logger=LoggerFactory.getLogger(WSSOAPProxyWrapper.class);

	private URL ServiceEndPoint = null;
	private ExecutionHandle Handle;
	private boolean SupportsExecutionContext;
	private ExecutionContextConfigBase SuppliedContextProxy;
	private String ID=null;

	public WSSOAPProxyWrapper(ExecutionHandle Handle,String ID, URL ServiceEndPoint, boolean SupportsExecutionContext,ExecutionContextConfigBase SuppliedContextProxy)
	{
		this.ServiceEndPoint = ServiceEndPoint;
		this.Handle = Handle;
		this.SupportsExecutionContext=SupportsExecutionContext;
		this.SuppliedContextProxy=SuppliedContextProxy;
		this.ID=ID;
	}
	
	private boolean IsKeepContextAlive()
	{
		return (this.SupportsExecutionContext && this.SuppliedContextProxy!=null && this.SuppliedContextProxy.KeepContextAlive);
	}

	public void Invoke(WSSOAPCall methodCall,ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException, ExecutionInternalErrorException, ExecutionSerializationException
	{
		NozzleHandler handler=null;
		boolean successfullyCompletedInvokation=false;
		try
		{
			methodCall.EvaluateArguments(Handle);
			WSSOAPSimpleProxy proxyWS=new WSSOAPSimpleProxy();
			//IWSPlugin plugin=WSPluginWrapper.LocatePlugin(this.PluginToUse);
			IChannelLocator locator= null;
			WSExecutionContextConfig conf=null;
			boolean provideContext=false;
			if(this.SupportsExecutionContext && this.SuppliedContextProxy!=null && (this.SuppliedContextProxy instanceof WSExecutionContextConfig))
			{
				provideContext=true;
				if(((WSExecutionContextConfig)this.SuppliedContextProxy).NozzleConfig!=null)
				{
					handler=new NozzleHandler();
					locator= handler.CreateInletNozzle(((WSExecutionContextConfig)this.SuppliedContextProxy).NozzleConfig, this.Handle);
				}
				conf=(WSExecutionContextConfig)this.SuppliedContextProxy;
			}
			Object ret=proxyWS.Invoke(this.ServiceEndPoint,methodCall,provideContext,locator,this.ID,conf,Handle);
			if(methodCall.OutputParameter!=null) methodCall.OutputParameter.SetParameterValue(Handle, ret);
			successfullyCompletedInvokation=true;
		}catch(ExecutionRunTimeException ex)
		{
			throw ex;
		}catch(ExecutionValidationException ex)
		{
			throw ex;
		}catch(ExecutionInternalErrorException ex)
		{
			throw ex;
		}catch(ExecutionSerializationException ex)
		{
			throw ex;
		}finally
		{
			logger.debug("Cleaning up plugin with null handler ("+(handler==null)+") and keep context alive ("+this.IsKeepContextAlive()+")");
			if(handler!=null)
			{
				if(successfullyCompletedInvokation)
				{
					if(this.IsKeepContextAlive())
					{
						this.Handle.AddContextHandler(handler);
					}
					else
					{
						handler.Dispose();
					}
				}
				else
				{
					handler.Dispose();
				}
			}
		}
	}
}
