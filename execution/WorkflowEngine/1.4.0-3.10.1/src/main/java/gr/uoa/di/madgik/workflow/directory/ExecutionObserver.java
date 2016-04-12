package gr.uoa.di.madgik.workflow.directory;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.reporter.ExecutionReporter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionObserver implements Observer
{
	private static final Logger logger=LoggerFactory.getLogger(ExecutionObserver.class);
	
	private static final long DefaultLease=1000*60*60*24*3;
	
	private final Object synchEvents=new Object();
	private String ExecutionID=null;
	private Queue<ExecutionStateEvent> Events=new LinkedList<ExecutionStateEvent>();
	private boolean isCompleted=false;
	private ExecutionHandle Handle=null;
	private ExecutionDirectory.DirectoryEntryType Type=ExecutionDirectory.DirectoryEntryType.JDL;
	private IWorkflowAdaptor Adaptor=null;
	private long EndTime=0;
	private long Lease=ExecutionObserver.DefaultLease;
	private boolean StorageSystemCleanupPerformed=false;
	private boolean reportMessages=false;
	private Object synchCompletion=null;
	
	public ExecutionObserver(String ExecutionID,ExecutionDirectory.DirectoryEntryType Type,long Lease,ExecutionHandle Handle,IWorkflowAdaptor Adaptor)
	{
		this.ExecutionID=ExecutionID;
		this.Handle=Handle;
		this.Type=Type;
		this.Adaptor=Adaptor;
		this.Lease=Lease;
		this.reportMessages=false;
	}
	
	public ExecutionObserver(String ExecutionID,ExecutionDirectory.DirectoryEntryType Type,long Lease,ExecutionHandle Handle,IWorkflowAdaptor Adaptor,boolean reportMessages)
	{
		this(ExecutionID, Type, Lease, Handle, Adaptor);
		this.reportMessages=reportMessages;
	}
	
	public ExecutionObserver(String ExecutionID,ExecutionDirectory.DirectoryEntryType Type,long Lease,ExecutionHandle Handle,IWorkflowAdaptor Adaptor,boolean reportMessages, Object synchCompletion)
	{
		this(ExecutionID, Type, Lease, Handle, Adaptor, reportMessages);
		this.synchCompletion = synchCompletion;
	}
	
	public boolean HasPerformedStorageSystermCleanup()
	{
		return this.StorageSystemCleanupPerformed;
	}
	
	public void PerformedStorageSystermCleanup()
	{
		this.StorageSystemCleanupPerformed=true;
		this.Handle.CleanUpStorageSystem();
	}
	
	public IWorkflowAdaptor GetAdaptor()
	{
		return this.Adaptor;
	}
	
	public ExecutionHandle GetExecutionHandle()
	{
		return this.Handle;
	}
	
	public ExecutionDirectory.DirectoryEntryType GetWorkflowType()
	{
		return this.Type;
	}
	
	public String GetExecutionID()
	{
		return this.ExecutionID;
	}
	
	public long GetLease()
	{
		if(this.Lease<=0) return ExecutionObserver.DefaultLease;
		return this.Lease;
	}
	
	public boolean ShouldCleanup()
	{
		if(!this.isCompleted) return false;
		if(EndTime==0) return false;
		long diff=Calendar.getInstance().getTimeInMillis()-EndTime;
		if(diff>this.GetLease()) return true;
		return false;
	}
	
	public boolean IsCompleted()
	{
		return this.isCompleted;
	}
	
	public List<ExecutionStateEvent> GetEvents()
	{
		List<ExecutionStateEvent> received=new ArrayList<ExecutionStateEvent>();
		synchronized (this.synchEvents)
		{
			while(!this.Events.isEmpty())
			{
				ExecutionStateEvent ev=this.Events.poll();
				if(ev==null) break;
				received.add(ev);
			}
		}
		return received;
	}
	
	public void update(Observable o, Object arg)
	{
		if (!o.getClass().getName().equals(arg.getClass().getName())) return;
		if (!(arg instanceof ExecutionStateEvent)) return;
		switch (((ExecutionStateEvent) arg).GetEventName())
		{
			case ExecutionCompleted:
			{
				logger.info("Execution "+this.ExecutionID+" got event " + arg.getClass().getSimpleName());
				if(this.synchCompletion!=null) 
				{
					synchronized(this.synchCompletion)
					{
						this.isCompleted=true;
						this.synchCompletion.notifyAll();
					}
				}
				this.isCompleted=true;
				this.EndTime=Calendar.getInstance().getTimeInMillis();
				if(this.reportMessages)
					ExecutionReporter.ReportExecutionStatus((ExecutionStateEvent)arg, this.ExecutionID, this.Type, this.Handle.GetPlan().EnvHints);
				break;
			}
			case ExecutionCancel:
			case ExecutionPause:
			case ExecutionResume:
			case ExecutionStarted:
			case ExecutionPerformance:
			case ExecutionExternalProgress:
			case ExecutionProgress:
			{
				logger.info("Execution "+this.ExecutionID+" got event " + arg.getClass().getSimpleName());
				if(this.reportMessages)
					ExecutionReporter.ReportExecutionStatus((ExecutionStateEvent)arg, this.ExecutionID, this.Type, this.Handle.GetPlan().EnvHints);
				break;
			}
			default:
			{
				logger.warn("Execution "+this.ExecutionID+" got unrecognized event " + arg.getClass().getSimpleName());
			}
		}
		synchronized(this.synchEvents)
		{
			this.Events.add((ExecutionStateEvent)arg);
		}
	}
	
	public void Cleanup()
	{
		if(this.Handle!=null) Handle.Cancel();
		synchronized (this.synchEvents)
		{
			this.Events.clear();
		}
	}
}
