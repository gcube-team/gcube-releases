package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.plan.element.invocable.IExecutionContext;
import gr.uoa.di.madgik.grs.proxy.IProxy;

public class SimpleExecutionContext implements IExecutionContext
{
	private ExecutionHandle Handle=null;
	private String ID=null;
	private String ExternalSender=null;
	private SimpleExecutionContextConfig SuppliedContextProxy=null;
	private IProxy Proxy;
	
	protected SimpleExecutionContext(ExecutionHandle Handle,String ID,String ExternalSender,SimpleExecutionContextConfig SuppliedContextProxy)
	{
		this.Handle=Handle;
		this.ID=ID;
		this.ExternalSender=ExternalSender;
		this.SuppliedContextProxy=SuppliedContextProxy;
	}
	
	public void Report(String Message)
	{
		Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.ID, this.ExternalSender, Message));
	}
	
	public void Report(int CurrentStep, int TotalSteps)
	{
		Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.ID, this.ExternalSender, CurrentStep,TotalSteps));
	}
	
	public void Report(int CurrentStep, int TotalSteps,String Message)
	{
		Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.ID, this.ExternalSender, CurrentStep,TotalSteps,Message));
	}
	
	public IProxy GetProxy()
	{
		if(this.Proxy==null)
		{
			if(this.SuppliedContextProxy!=null) this.Proxy=this.SuppliedContextProxy.GetProxy();
		}
		return this.Proxy;
	}

	public void Close()
	{
		//no need to close something
	}
}
