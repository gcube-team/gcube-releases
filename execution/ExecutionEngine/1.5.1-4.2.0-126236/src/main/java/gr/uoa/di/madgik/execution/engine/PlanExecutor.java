package gr.uoa.di.madgik.execution.engine;

import gr.uoa.di.madgik.execution.event.ExecutionCompletedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStartedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.report.accounting.JobAccountingDispatcher;
import gr.uoa.di.madgik.execution.report.monitoring.MonitoringDispatcher;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the execution of an {@link ExecutionPlan}. Its role is to start in a background 
 * thread the execution of a plan and monitor its status until it is completed. Once the execution
 * is completed, the instance removes it self from the list of {@link PlanExecutor}s the engine
 * references and cleans up any local files that are marked for cleanup in the plan.
 * 
 * @author gpapanikos
 */
public class PlanExecutor extends Thread implements Observer
{

	/** The logger. */
	private Logger logger=LoggerFactory.getLogger(PlanExecutor.class);
	
	/** The Handle. */
	private ExecutionHandle Handle=null;
	
	/** Used for monitoring asynchronous notifications */
	private MonitoringDispatcher monitor;
	
	private JobAccountingDispatcher accounter;
	
	/**
	 * Instantiates a new plan executor.
	 * 
	 * @param Handle the handle
	 */
	public PlanExecutor (ExecutionHandle Handle)
	{
		this.Handle=Handle;
		this.RegisterForEvents();
		this.setName(PlanExecutor.class.getName());
		this.setDaemon(true);
		
		monitor = new MonitoringDispatcher(Handle.GetPlan().EnvHints);
		accounter = new JobAccountingDispatcher(Handle);
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the execution handle
	 */
	public ExecutionHandle GetHandle()
	{
		return this.Handle;
	}
	
	/**
	 * Register for events.
	 */
	private void RegisterForEvents()
	{
		ExecutionStateEvent ev= this.Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionCompleted);
		if(ev!=null) ev.addObserver(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		monitor.commit();
		try
		{
			logger.debug("running executor");
			this.Handle.PlanRunning();
			this.Handle.EmitEvent(new ExecutionStartedStateEvent());
			this.Handle.GetPlan().Root.Execute(this.Handle);
			this.Handle.SetIsCompleted(true);
			this.Handle.SetIsCompletedWithError(false);
			this.Handle.SetIsCompletedWithSuccess(true);
			this.Handle.SetCompletionError(null);
		}catch(ExecutionException ex)
		{
			logger.debug("Could not complete successfully the plan execution",ex);
			this.Handle.SetIsCompleted(true);
			this.Handle.SetIsCompletedWithError(true);
			this.Handle.SetIsCompletedWithSuccess(false);
			this.Handle.SetCompletionError(ex);
		} finally {
			monitor.commit();
			accounter.commit();
		}
		this.Handle.PlanCompleted();
		this.Handle.EmitEvent(new ExecutionCompletedStateEvent());
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if(!o.getClass().getName().equals(arg.getClass().getName()))
		{
			return;
		}
		if(arg instanceof ExecutionCompletedStateEvent)
		{
			ExecutionEngine.RemoveExecutor(this);
		}
	}
	
	/**
	 * Unregisters itself from registered events, cleans up local files that are marked for cleanup
	 * in the plan and calls {@link ExecutionHandle#Dispose()} 
	 */
	public void Dispose()
	{
		ExecutionStateEvent ev= this.Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionCompleted);
		if(ev!=null) ev.deleteObserver(this);
		this.CleanUpFiles(/*this.Handle*/);
		this.Handle.Dispose();
	}
	
	/**
	 * Clean up files.
	 */
	private void CleanUpFiles(/*ExecutionHandle Handle*/)
	{
		for(String s : this.Handle.GetPlan().CleanUpLocalFiles)
		{
			try
			{
//				String file=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(s).Value.GetValue());
				String file=s;
				File f=new File(file);
				if(f.exists() && f.isFile()) if(!f.delete()) throw new Exception("Delete operation returned false. File prossibly not deleted");
			}
			catch(Exception ex)
			{
//				logger.warn("Could not cleanup file of variable with id "+s);
				logger.warn("Could not cleanup file of variable with location "+s);
			}
		}
	}
}
