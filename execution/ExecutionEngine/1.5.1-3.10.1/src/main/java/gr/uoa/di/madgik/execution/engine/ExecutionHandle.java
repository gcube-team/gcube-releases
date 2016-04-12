package gr.uoa.di.madgik.execution.engine;

import gr.uoa.di.madgik.execution.event.ExecutionCancelStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionCompletedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPauseStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionResumeStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStartedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.invocable.NozzleHandler;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.ss.StorageSystem;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExecutionHandle.
 * 
 * @author gpapanikos
 */
public class ExecutionHandle implements Observer, Serializable
{
	
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(ExecutionHandle.class);
	
	/**
	 * The state of the plan
	 * 
	 * @author gpapanikos
	 */
	public enum HandleState
	{
		
		/** Ready state. */
		Ready,
		
		/** Running state. */
		Running,
		
		/** Paused state. */
		Paused,
		
		/** Completed state. */
		Completed,
		
		/** Canceled state. */
		Cancel
	}
	
	/** The Events. */
	private Hashtable<ExecutionStateEvent.EventName, ExecutionStateEvent> Events = new Hashtable<ExecutionStateEvent.EventName, ExecutionStateEvent>();
	
	/** The Plan. */
	private ExecutionPlan Plan=null;
	
	/** The State. */
	private HandleState State=HandleState.Ready;
	
	/** Whether the plan is Completed. */
	private boolean Completed=false;
	
	/** Whether the plan Completed with success. */
	private boolean CompletedWithSuccess=false;
	
	/** Whether the plan Completed with error. */
	private boolean CompletedWithError=false;
	
	/** The error with which the plan Completed. */
	private ExecutionException CompletionError=null;
	
	/** Synchronization object. */
	private final Boolean lockMe=new Boolean(false);
	
	/** A container of context handlers to keep alive until the plan completes. */
	private Set<NozzleHandler> AliveContextHandlers=new HashSet<NozzleHandler>();
	
	/** Isolation info relative to the node this handle monitors. */
	private BoundaryIsolationInfo IsolationInfo=null;
	
	/** Synchronization object for the actions running within the node this handle monitors. */
	private Object SynchActionsRunning=new Object();
	
	/** The number of running Actions running within the node this handle monitors. */
	private int ActionsRunningWithingBoundary=0;
	
	/** Hosting node information */ 
	private String hostingNodeInfo = null;
	
	/**
	 * Instantiates a new execution handle.
	 * 
	 * @param Plan the plan
	 */
	public ExecutionHandle(ExecutionPlan Plan, String hostingNodeInfo)
	{
		this.Init();
		this.Plan=Plan;
		this.hostingNodeInfo = hostingNodeInfo;
	}
	
	public String getHostingNodeInfo() {
		return this.hostingNodeInfo;
	}
	
	/**
	 * Increase actions running.
	 */
	public void IncreaseActionsRunning()
	{
		this.ActionsRunningWithingBoundary+=1;
	}
	
	/**
	 * Decrease actions running.
	 */
	public void DecreaseActionsRunning()
	{
		this.ActionsRunningWithingBoundary-=1;
		this.GetSynchActionsRunning().notifyAll();
	}
	
	/**
	 * Force re-check of pending actions.
	 */
	public void ForceRecheckOfPendingActions()
	{
		synchronized (this.GetSynchActionsRunning())
		{
			this.GetSynchActionsRunning().notifyAll();
		}
	}
	
	/**
	 * Gets the synch actions running synchronization object
	 * 
	 * @return the object
	 */
	public Object GetSynchActionsRunning()
	{
		return this.SynchActionsRunning;
	}
	
	/**
	 * Gets the number of actions running.
	 * 
	 * @return the number
	 */
	public int GetActionsRunning()
	{
		return this.ActionsRunningWithingBoundary;
	}
	
	/**
	 * Checks if is completed.
	 * 
	 * @return true, if successful
	 */
	public boolean IsCompleted()
	{
		return this.Completed;
	}
	
	/**
	 * Adds the context handler.
	 * 
	 * @param Handler the handler
	 */
	public void AddContextHandler(NozzleHandler Handler)
	{
		this.AliveContextHandlers.add(Handler);
	}
	
	/**
	 * Gets the isolated file.
	 * 
	 * @param original the original
	 * 
	 * @return the file
	 */
	public File GetIsolatedFile(File original)
	{
		if(!this.IsIsolationRequested()) return original;
		if(original.isAbsolute()) return original;
		return new File(this.GetIsolationInfo().GetBaseDirFile(),original.toString());
	}
	
	/**
	 * Checks if is isolation requested.
	 * 
	 * @return true, if successful
	 */
	public boolean IsIsolationRequested()
	{
		if(this.IsolationInfo==null) return false;
		return this.IsolationInfo.Isolate;
	}
	
	/**
	 * Sets the isolation info.
	 * 
	 * @param IsolationInfo the isolation info
	 */
	public void SetIsolationInfo(BoundaryIsolationInfo IsolationInfo)
	{
		this.IsolationInfo=IsolationInfo;
	}
	
	/**
	 * Gets the isolation info.
	 * 
	 * @return the boundary isolation info
	 */
	public BoundaryIsolationInfo GetIsolationInfo()
	{
		return this.IsolationInfo;
	}
	
	/**
	 * Initialize isolation.
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 * @throws ExecutionRunTimeException A runtime error occurred
	 */
	public void InitializeIsolation() throws ExecutionValidationException, ExecutionRunTimeException
	{
		this.IsolationInfo.InitializeIsolation(this,this.Plan.EnvHints);
	}
	
	/**
	 * Finalize isolation.
	 * 
	 * @throws ExecutionValidationException An execution validation error occurred
	 * @throws ExecutionRunTimeException An runtime error occurred
	 */
	public void FinalizeIsolation() throws ExecutionValidationException, ExecutionRunTimeException
	{
		this.IsolationInfo.FinalizeIsolation(this,this.Plan.EnvHints);
	}
	
	/**
	 * Sets the is completed.
	 * 
	 * @param IsCompleted the is completed
	 */
	protected void SetIsCompleted(boolean IsCompleted)
	{
		this.Completed=IsCompleted;
	}
	
	/**
	 * Checks if is completed with success.
	 * 
	 * @return true, if successful
	 */
	public boolean IsCompletedWithSuccess()
	{
		return this.CompletedWithSuccess;
	}
	
	/**
	 * Sets the is completed with success.
	 * 
	 * @param IsCompletedWithSuccess the is completed with success
	 */
	protected void SetIsCompletedWithSuccess(boolean IsCompletedWithSuccess)
	{
		this.CompletedWithSuccess=IsCompletedWithSuccess;
	}
	
	/**
	 * Checks if is completed with error.
	 * 
	 * @return true, if successful
	 */
	public boolean IsCompletedWithError()
	{
		return this.CompletedWithError;
	}
	
	/**
	 * Sets the is completed with error.
	 * 
	 * @param IsCompletedWithError the is completed with error
	 */
	protected void SetIsCompletedWithError(boolean IsCompletedWithError)
	{
		this.CompletedWithError=IsCompletedWithError;
	}
	
	/**
	 * Gets the completion error.
	 * 
	 * @return the execution exception
	 */
	public ExecutionException GetCompletionError()
	{
		return this.CompletionError;
	}
	
	/**
	 * Sets the completion error.
	 * 
	 * @param CompletionError the completion error
	 */
	protected void SetCompletionError(ExecutionException CompletionError)
	{
		this.CompletionError=CompletionError;
	}
	
	/**
	 * Initializes the handle
	 */
	private void Init() 
	{
		if(this.Events.size()!=0) throw new IllegalStateException("Execution handle has already been initialized");
		this.Events.put(ExecutionStateEvent.EventName.ExecutionCompleted, new ExecutionCompletedStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionStarted, new ExecutionStartedStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionPause, new ExecutionPauseStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionResume, new ExecutionResumeStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionCancel, new ExecutionCancelStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionProgress, new ExecutionProgressReportStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionExternalProgress, new ExecutionExternalProgressReportStateEvent());
		this.Events.put(ExecutionStateEvent.EventName.ExecutionPerformance, new ExecutionPerformanceReportStateEvent());
		this.Events.get(ExecutionStateEvent.EventName.ExecutionPause).addObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionResume).addObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionCancel).addObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionCompleted).addObserver(this);
	}
	
	/**
	 * Register observer for the events the handle emits
	 * 
	 * @param HandleObserver the handle observer
	 */
	public void RegisterObserver(Observer HandleObserver)
	{
		for(ExecutionStateEvent ev : this.GetEvents())
		{
			ev.addObserver(HandleObserver);
		}
	}

	/**
	 * Emit event.
	 * 
	 * @param Event the event
	 */
	public void EmitEvent(ExecutionStateEvent Event)
	{
		if(this.Plan.Config.ChokeProgressReporting && 
				(Event.GetEventName().equals(ExecutionStateEvent.EventName.ExecutionProgress) || 
				Event.GetEventName().equals(ExecutionStateEvent.EventName.ExecutionExternalProgress)) ||
			this.Plan.Config.ChokePerformanceReporting && 
				Event.GetEventName().equals(ExecutionStateEvent.EventName.ExecutionPerformance))
		{
			return;
		}
		synchronized (this.lockMe)
		{
			ExecutionStateEvent e = this.GetEvent(Event.GetEventName());
			if (e == null)
			{
				return;
			}
			e.SetChanged();
			e.notifyObservers(Event);
		}
	}

	/**
	 * Gets the event.
	 * 
	 * @param Event the event
	 * 
	 * @return the execution state event
	 */
	public ExecutionStateEvent GetEvent(ExecutionStateEvent.EventName Event)
	{
		return this.Events.get(Event);
	}
	
	/**
	 * Gets the events.
	 * 
	 * @return the collection< execution state event>
	 */
	public Collection<ExecutionStateEvent> GetEvents()
	{
		return this.Events.values();
	}
	
	/**
	 * Gets the plan.
	 * 
	 * @return the execution plan
	 */
	public ExecutionPlan GetPlan()
	{
		return this.Plan;
	}
	
	/**
	 * Gets the handle state.
	 * 
	 * @return the execution handle. handle state
	 */
	public ExecutionHandle.HandleState GetHandleState()
	{
		return this.State;
	}
	
	/**
	 * Plan running.
	 */
	protected void PlanRunning()
	{
		this.State=HandleState.Running;
	}
	
	/**
	 * Plan completed.
	 */
	protected void PlanCompleted()
	{
		this.State=HandleState.Completed;
	}
	
	/**
	 * Pause.
	 */
	public void Pause()
	{
		this.State=HandleState.Paused;
		this.EmitEvent(new ExecutionPauseStateEvent());
	}
	
	/**
	 * Resume.
	 */
	public void Resume()
	{
		this.State=HandleState.Running;
		this.EmitEvent(new ExecutionResumeStateEvent());
	}
	
	/**
	 * Cancel.
	 */
	public void Cancel()
	{
		this.State=HandleState.Cancel;
		this.EmitEvent(new ExecutionCancelStateEvent());
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
		if(arg instanceof ExecutionPauseStateEvent)
		{
			this.State=HandleState.Paused;
		}
		if(arg instanceof ExecutionResumeStateEvent)
		{
			this.State=HandleState.Running;
		}
		if(arg instanceof ExecutionCancelStateEvent)
		{
			this.State=HandleState.Cancel;
			this.ForceRecheckOfPendingActions();
		}
		if(arg instanceof ExecutionCompletedStateEvent)
		{
			this.ForceRecheckOfPendingActions();
		}
	}
	
	/**
	 * Dispose this instance, unregisters from registered events, and disposes the context nozzles that
	 * were requested to be kept alive until the execution of the handle was completed.
	 */
	public void Dispose()
	{
		this.ForceRecheckOfPendingActions();
		this.Events.get(ExecutionStateEvent.EventName.ExecutionPause).deleteObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionResume).deleteObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionCancel).deleteObserver(this);
		this.Events.get(ExecutionStateEvent.EventName.ExecutionCompleted).deleteObserver(this);
		for(ExecutionStateEvent ev : this.GetEvents()) ev.deleteObservers();
		logger.debug("Disposing alive context ("+this.AliveContextHandlers.size()+")");
		for(NozzleHandler handler : this.AliveContextHandlers)
		{
			try
			{
				handler.Dispose();
			} catch (ExecutionInternalErrorException e)
			{
				logger.debug("Problem disposing context");
			}
		}
		this.AliveContextHandlers.clear();
	}
	
	public void CleanUpStorageSystem()
	{
		logger.debug("Cleaning up StorageSystem");
		Set<String> Exclude=new HashSet<String>();
		for(String s : this.Plan.CleanUpSSExclude)
		{
			if(this.GetPlan().Variables.Get(s)==null) continue;
			if(!this.GetPlan().Variables.Get(s).IsAvailable) continue;
			try{Exclude.add(DataTypeUtils.GetValueAsString(this.GetPlan().Variables.Get(s).Value.GetValue()));}catch(Exception ex){}
		}
		for(String s : this.GetPlan().CleanUpSS)
		{
			if(Exclude.contains(s)) continue;
			try
			{
				logger.debug("Cleaning up Storage System of ID "+s);
				StorageSystem.Delete(s, this.GetPlan().EnvHints);
			}catch(Exception ex)
			{
				logger.warn("Could not remove from Storage System id "+s,ex);
			}
		}
	}
}
