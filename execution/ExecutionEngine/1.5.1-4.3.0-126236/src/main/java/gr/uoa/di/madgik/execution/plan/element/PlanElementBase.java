package gr.uoa.di.madgik.execution.plan.element;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle.HandleState;
import gr.uoa.di.madgik.execution.event.ExecutionCancelStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionCompletedStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionResumeStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReactionHandler;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction.ReactionType;
import gr.uoa.di.madgik.execution.report.accounting.JobAccountingDispatcher;
import gr.uoa.di.madgik.execution.report.accounting.TaskAccountingDispatcher;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract class implementing {@link IPlanElement}. This class can be extended by other classes that will 
 * offer some functionality as plan elements. This class offers some common functionalities needed by all
 * plan classes. When the {@link IPlanElement#Execute(ExecutionHandle)} is invoked, the 
 * {@link IPlanElement#ValidatePreExecution(ExecutionHandle)} is invoked, the element is registered for
 * {@link ExecutionStateEvent.EventName#ExecutionCancel}, {@link ExecutionStateEvent.EventName#ExecutionPause}
 * and {@link ExecutionStateEvent.EventName#ExecutionResume} events and finally the {@link PlanElementBase#ExecuteExtender(ExecutionHandle)}
 * method is invoked. In case an error occurs during this call, the element's {@link ContingencyTrigger} collection
 * is retrieved and processed to check if there are some reactions that can be taken.
 * 
 * @author gpapanikos
 */
public abstract class PlanElementBase implements IPlanElement, Observer
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Synchronization object used to resume execution */
	private final Boolean Resume = new Boolean(false);
	
	/**
	 * The type of clock associated with {@link PlanElementBase#StartClock(ClockType)}
	 * and {@link PlanElementBase#StopClock(ClockType)}
	 */
	protected enum ClockType
	{
		
		/** Initialization timing */
		Init,
		
		/** Finalization timing */
		Finilization,
		
		/** Timing the children of the element */
		Children,
		
		/** The Total time */
		Total,
		
		/** Sub Calls time */
		Call
	}
	
	/** The start initialization time */
	private long startInit=0;
	
	/** The sum of initialization time */
	private long sumInit=0;
	
	/** The start finalization time. */
	private long startFinilize=0;
	
	/** The sum of finalization time. */
	private long sumFinilize=0;
	
	/** The start of the children execution time */
	private long startChildren=0;
	
	/** The sum of the children execution time */
	private long sumChildren=0;
	
	/** The start of total execution time */
	private long startTotal=0;
	
	/** The sum of total execution time */
	private long sumTotal=0;
	
	/** The start of sub call time */
	private long startCall=0;
	
	/** The sum of sub call time */
	private long sumCall=0;
	
	/** The sub call count. */
	private int callCount=0;
	
	/**
	 * Reset clocks.
	 */
	protected void ResetClocks()
	{
		sumInit=0;
		sumFinilize=0;
		sumChildren=0;
		sumTotal=0;
		sumCall=0;
		callCount=0;
	}
	
	/**
	 * Start clock.
	 * 
	 * @param Type the type
	 */
	protected void StartClock(ClockType Type)
	{
		switch (Type)
		{
			case Children:
			{
				startChildren=Calendar.getInstance().getTimeInMillis();
				break;
			}
			case Finilization:
			{
				startFinilize=Calendar.getInstance().getTimeInMillis();
				break;
			}
			case Init:
			{
				startInit=Calendar.getInstance().getTimeInMillis();
				break;
			}
			case Total:
			{
				startTotal=Calendar.getInstance().getTimeInMillis();
				break;
			}
			case Call:
			{
				startCall=Calendar.getInstance().getTimeInMillis();
				break;
			}
		}
	}
	
	/**
	 * Stop clock.
	 * 
	 * @param Type the type
	 */
	protected void StopClock(ClockType Type)
	{
		switch (Type)
		{
			case Children:
			{
				sumChildren+=(Calendar.getInstance().getTimeInMillis()-startChildren);
				break;
			}
			case Finilization:
			{
				sumFinilize+=(Calendar.getInstance().getTimeInMillis()-startFinilize);
				break;
			}
			case Init:
			{
				sumInit+=(Calendar.getInstance().getTimeInMillis()-startInit);
				break;
			}
			case Total:
			{
				sumTotal+=(Calendar.getInstance().getTimeInMillis()-startTotal);
				break;
			}
			case Call:
			{
				sumCall+=(Calendar.getInstance().getTimeInMillis()-startCall);
				callCount+=1;
				break;
			}
		}
	}
	
	/**
	 * Instantiates a new performance event populating it with the clocks it has calculated
	 * 
	 * @return the execution performance report state event
	 */
	protected ExecutionPerformanceReportStateEvent GetPerformanceEvent()
	{
		return new ExecutionPerformanceReportStateEvent(this.GetID(), sumTotal, sumInit, sumFinilize, sumChildren,callCount,sumCall);
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Execute(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void Execute(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		try
		{
			this.GetExtenderLogger().debug("Validating Plan Element");
			this.ValidatePreExecution(Handle);
			TaskAccountingDispatcher accounter = null;
			accounter = new TaskAccountingDispatcher(Handle);
			this.GetExtenderLogger().debug("Executing Plan Element");
			this.ExecuteWithStateAwareness(Handle);
			try {
				accounter.commit();
			}catch (Exception e) {}
		} catch (Exception ex)
		{
			String causeString="";
			if(ex instanceof ExecutionRunTimeException) causeString=" and cause "+((ExecutionRunTimeException)ex).GetCauseFullName();
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(), "Execution of element "+this.GetName()+" failed with error "+ex.getClass().getName()+"("+ex.getMessage()+")"+causeString+". Checking for contingency plans"));
			this.GetExtenderLogger().debug("Checking if triggers are supported");
			if (!this.SupportsContingencyTriggers()) ExceptionUtils.ThrowTransformedException(ex);
			this.GetExtenderLogger().debug("Checking if triggers are defined");
			if (this.GetContingencyTriggers() == null || this.GetContingencyTriggers().size() == 0) ExceptionUtils.ThrowTransformedException(ex);
			Map<String, String> supportedTriggers = this.GetSupportedContingencyReactionTypes();
			boolean handled=false;
			for (ContingencyTrigger trig : this.GetContingencyTriggers())
			{
				this.GetExtenderLogger().debug("Examining trigger "+trig.Reaction.GetReactionType().toString());
				if (!supportedTriggers.containsKey(trig.Reaction.GetReactionType().toString())) continue;
				this.GetExtenderLogger().debug("Checking if trigger can handle error");
				if (!trig.CanHandleError(ex)) continue;
				this.GetExtenderLogger().debug("Passing error to handler");
				IContingencyReactionHandler ReactionHandler = trig.Reaction.GetReactionHandler();
				ReactionHandler.Handle(this.GetID(),ex, Handle, this);
				handled=true;
			}
			if(!handled) ExceptionUtils.ThrowTransformedException(ex);
		}
	}

	/**
	 * Execute with state awareness. The element is registered for {@link ExecutionStateEvent.EventName#ExecutionCancel}, 
	 * {@link ExecutionStateEvent.EventName#ExecutionPause} and {@link ExecutionStateEvent.EventName#ExecutionResume} events,
	 * the {@link PlanElementBase#ExecuteExtender(ExecutionHandle)} is called, and then the element is again unregistered
	 * from these events 
	 * 
	 * @param Handle the execution handle
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution was canceled
	 * @throws ExecutionBreakException The execution was terminated 
	 */
	public void ExecuteWithStateAwareness(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		try
		{
			this.InitExecution(Handle);
			this.ExecuteExtender(Handle);
		} catch (Exception ex)
		{
			this.FinilizeExecution(Handle);
			ExceptionUtils.ThrowTransformedException(ex);
		}
	}

	/**
	 * Gets the extender logger.
	 * 
	 * @return the logger
	 */
	protected abstract Logger GetExtenderLogger();

	/**
	 * Execute extender. This class is implemented by extenders of the class
	 * 
	 * @param Handle the execution handle
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution was canceled
	 * @throws ExecutionBreakException The execution was terminated 
	 */
	protected abstract void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException;

	/**
	 * Check status of the execution. If the plan was declared as paused this invocation will block until a 
	 * resume event is caught.
	 * 
	 * @param Handle the execution handle
	 * 
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution was canceled
	 */
	protected void CheckStatus(ExecutionHandle Handle) throws ExecutionCancelException, ExecutionInternalErrorException
	{
		while (!this.CheckExecutionStatus(Handle))
		{
			this.WaitToResume();
		}
	}

	/**
	 * Gets the supported contingency reaction types.
	 * 
	 * @return the supported reaction types
	 */
	private Map<String, String> GetSupportedContingencyReactionTypes()
	{
		Map<String, String> supportedTriggers = new HashMap<String, String>();
		ReactionType[] supportedTypes = this.SupportedContingencyTriggers();
		for (ReactionType rt : supportedTypes)
			supportedTriggers.put(rt.toString(), null);
		return supportedTriggers;
	}

	/**
	 * Initializes the execution of the element by registering the element to needed events
	 * 
	 * @param Handle the execution handle
	 */
	private void InitExecution(ExecutionHandle Handle)
	{
		this.RegisterToHandle(Handle);
	}

	/**
	 * Finalizes the execution by unregistering the element for the events it was previously registered
	 * 
	 * @param Handle the execution handle
	 */
	private void FinilizeExecution(ExecutionHandle Handle)
	{
		this.UnregisterFromHandle(Handle);
	}

	/**
	 * Register to execution handle events
	 * 
	 * @param Handle the execution handle
	 */
	private void RegisterToHandle(ExecutionHandle Handle)
	{
		ExecutionStateEvent ev = Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionResume);
		if (ev != null) ev.addObserver(this);
		ev = Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionCancel);
		if (ev != null) ev.addObserver(this);
		ev = Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionCompleted);
		if (ev != null) ev.addObserver(this);
	}

	/**
	 * Unregister from execution handle events
	 * 
	 * @param Handle the execution handle
	 */
	private void UnregisterFromHandle(ExecutionHandle Handle)
	{
		ExecutionStateEvent ev = Handle.GetEvent(ExecutionStateEvent.EventName.ExecutionResume);
		if (ev != null) ev.deleteObserver(this);
	}

	/**
	 * Check execution status.
	 * 
	 * @param Handle the execution handle
	 * 
	 * @return true, if the execution should continue
	 * 
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution was canceled
	 */
	private boolean CheckExecutionStatus(ExecutionHandle Handle) throws ExecutionCancelException, ExecutionInternalErrorException
	{
		switch (Handle.GetHandleState())
		{
			case Cancel:
			{
				this.GetExtenderLogger().debug("Execution canceled");
				throw new ExecutionCancelException("Execution canceled in element " + this.GetName());
			}
			case Completed:
			{
				this.GetExtenderLogger().debug("Execution completed while elements shill active");
				throw new ExecutionInternalErrorException("Execution completed while elements shill active " + this.GetName());
			}
			case Paused:
			{
				return false;
			}
			case Ready:
			{
				this.GetExtenderLogger().debug("Execution ready and not running while elements shill active");
				throw new ExecutionInternalErrorException("Execution ready and not running while elements shill active " + this.GetName());
			}
			case Running:
			{
				return true;
			}
			default:
			{
				this.GetExtenderLogger().debug("Unrecognized execution state " + Handle.GetHandleState().toString());
				throw new ExecutionInternalErrorException("Unrecognized execution state " + Handle.GetHandleState().toString() + " " + this.GetName());
			}
		}
	}

	/**
	 * Wait to resume.
	 */
	private void WaitToResume()
	{
		synchronized (this.Resume)
		{
			try
			{
				this.Resume.wait();
			} catch (Exception ex)
			{
			}
			;
		}
	}
	
	/**
	 * Register to running action elements restriction.
	 * 
	 * @param Handle the execution handle
	 */
	public void RegisterToRunningActionElementsRestriction(ExecutionHandle Handle)
	{
		if(!Handle.GetPlan().Config.RestrictActionTypes.contains(this.GetPlanElementType())) return;
		if(Handle.GetPlan().Config.ConcurrentActionsPerBoundary>0)
		{
			synchronized (Handle.GetSynchActionsRunning())
			{
				while(true)
				{
					if(Handle.GetHandleState()!=HandleState.Running) break;
					if(Handle.GetActionsRunning()<=Handle.GetPlan().Config.ConcurrentActionsPerBoundary-1)
					{
						this.GetExtenderLogger().debug("Granting execution with number of running elements before current is counted beeing "+Handle.GetActionsRunning()+" of "+Handle.GetPlan().Config.ConcurrentActionsPerBoundary);
						Handle.IncreaseActionsRunning();
						break;
					}
					else
					{
						this.GetExtenderLogger().debug("Blocking execution until some action element is completed");
						try{ Handle.GetSynchActionsRunning().wait(); }catch(Exception ex){}
					}
				}
			}
		}
	}

	/**
	 * Unregister to running action elements restriction.
	 * 
	 * @param Handle the handle
	 */
	public void UnregisterToRunningActionElementsRestriction(ExecutionHandle Handle)
	{
		if(!Handle.GetPlan().Config.RestrictActionTypes.contains(this.GetPlanElementType())) return;
		synchronized (Handle.GetSynchActionsRunning())
		{
			this.GetExtenderLogger().debug("Decreasing number of running action elements which are before decrease "+Handle.GetActionsRunning()+" of "+Handle.GetPlan().Config.ConcurrentActionsPerBoundary);
			Handle.DecreaseActionsRunning();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (!o.getClass().getName().equals(arg.getClass().getName())) return;
		if ((arg instanceof ExecutionResumeStateEvent) || (arg instanceof ExecutionCancelStateEvent) || (arg instanceof ExecutionCompletedStateEvent))
		{
			synchronized (this.Resume)
			{
				this.Resume.notify();
			}
		}

	}
}
