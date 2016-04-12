package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class WrapperObserver implements Observer 
{

	private ExecutionHandle Handle = null;
	private static Logger logger = LoggerFactory.getLogger(WrapperObserver.class);
	private static final Object synchCompletion=new Object();
	
	public WrapperObserver(ExecutionHandle handle) 
	{
		this.Handle = handle;
	}
	
	protected boolean EvaluateResult() throws ExecutionSerializationException
	{
		if(!Handle.IsCompleted())
		{
			logger.warn("Not completed! Why am I here?");
			return false;
		}
		else if(Handle.IsCompletedWithSuccess())
		{
			logger.info("Plan successfully completed");
			return true;
		}
		else if(Handle.IsCompletedWithError()) 
		{
			String errorString="Plan unsuccessfully completed with error";
			if(Handle.GetCompletionError() instanceof ExecutionRunTimeException) errorString+=" of cause "+((ExecutionRunTimeException)Handle.GetCompletionError()).GetCauseFullName();
			logger.info(errorString,Handle.GetCompletionError());
			return false;
		}
		else
		{
			logger.warn("Completed but neither with success or failure!");
			return false;
		}
	}
	
	public Object getSynchCompletionObject() 
	{
		return synchCompletion;
	}
	
	public void update(Observable o, Object arg)
	{
		if (!o.getClass().getName().equals(arg.getClass().getName())) return;
		if (!(arg instanceof ExecutionStateEvent)) return;
		switch (((ExecutionStateEvent) arg).GetEventName())
		{
			case ExecutionCompleted:
			{
				logger.info("Received event " + arg.getClass().getSimpleName());
				synchronized (synchCompletion)
				{
					synchCompletion.notify();
				}
				break;
			}
			case ExecutionCancel:
			case ExecutionPause:
			case ExecutionResume:
			case ExecutionStarted:
			{
				logger.info("Received event " + arg.getClass().getSimpleName());
				break;
			}
			case ExecutionProgress:
			{
				ExecutionProgressReportStateEvent ev = (ExecutionProgressReportStateEvent) arg;
				String report = "";
				if (ev.DoesReportProgress()) report = ev.GetCurrentStep() + "/" + ev.GetTotalSteps();
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				logger.info("sender (" + sender + ") progress(" + report + ") message (" + msg + ")");
				break;
			}
			case ExecutionExternalProgress:
			{
				ExecutionExternalProgressReportStateEvent ev = (ExecutionExternalProgressReportStateEvent) arg;
				String report = "";
				if (ev.DoesReportProgress()) report = ev.GetCurrentStep() + "/" + ev.GetTotalSteps();
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				String extSender = "";
				if (ev.GetExternalSender() != null) extSender = ev.GetExternalSender();
				logger.info("sender (" + sender + ") external sender (" + extSender + ") progress(" + report + ") message (" + msg + ")");
				break;
			}
			case ExecutionPerformance:
			{
				ExecutionPerformanceReportStateEvent ev = (ExecutionPerformanceReportStateEvent) arg;
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String subCalls="";
				if(ev.GetSubCalls() !=0)
				{
					subCalls="including "+ev.GetSubCalls()+" sub calls of total time "+ev.GetSubCallTotalTime();
				}
				logger.info("sender (" + sender + ") reports performance ( Total "+ev.GetTotalTime()+", Initialization "+ev.GetInitializationTime()+", Finilization "+ev.GetFinilizationTime()+" Children "+ev.GetChildrenTotalTime()+") miliseconds "+subCalls);
				break;
			}
			default:
			{
				logger.warn("Received unrecognized event type " + ((ExecutionStateEvent) arg).GetEventName().toString());
			}
		}
	}
}
