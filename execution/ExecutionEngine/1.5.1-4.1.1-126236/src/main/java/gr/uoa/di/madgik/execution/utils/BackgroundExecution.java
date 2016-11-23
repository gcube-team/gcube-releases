package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;

public class BackgroundExecution implements Runnable
{
	private ExecutionHandle Handle;
	public IPlanElement Element;
	public ExecutionException Error = null;
	private Boolean synchWorker=null;
	public boolean ExecutionCompleted=false;

	public BackgroundExecution(IPlanElement Element, ExecutionHandle Handle,Boolean synchWorker)
	{
		this.Element=Element;
		this.Handle=Handle;
		this.synchWorker=synchWorker;
	}

	public void run()
	{
		try
		{
			this.Element.Execute(Handle);
		} catch (ExecutionException ex)
		{
			this.Error = ex;
		}
		synchronized (this.synchWorker)
		{
			this.ExecutionCompleted=true;
			this.synchWorker.notify();
		}
	}
}
