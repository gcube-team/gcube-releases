package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class ExecutionObserver implements Observer
{
	private static Logger logger = LoggerFactory.getLogger(ExecutionObserver.class);
	
	public static Map<IPlanElement.PlanElementType,PlanElementPerformanceMetrics> PerformanceMetrics=new HashMap<IPlanElement.PlanElementType, PlanElementPerformanceMetrics>();
	private static final Object synchStats=new Object();

	private ExecutionHandle Handle = null;
	private Object synchCompletion = null;

	public ExecutionObserver(ExecutionHandle Handle, Object synchCompletion)
	{
		this.Handle = Handle;
		this.synchCompletion = synchCompletion;
	}

	public void update(Observable o, Object arg)
	{
		if (!o.getClass().getName().equals(arg.getClass().getName())) return;
		if (!(arg instanceof ExecutionStateEvent)) return;
		switch (((ExecutionStateEvent) arg).GetEventName())
		{
			case ExecutionCompleted:
			{
				synchronized (this.synchCompletion)
				{
					this.synchCompletion.notify();
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
					if(this.Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = this.Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				logger.info("Sender (" + sender + ") progress(" + report + ") message (" + msg + ")");
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
					if(this.Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = this.Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				String extSender = "";
				if (ev.GetExternalSender() != null) extSender = ev.GetExternalSender();
				logger.info("Sender (" + sender + ") external sender (" + extSender + ") progress(" + report + ") message (" + msg + ")");
				break;
			}
			case ExecutionPerformance:
			{
				ExecutionPerformanceReportStateEvent ev=(ExecutionPerformanceReportStateEvent) arg;
				if(ev.GetID()== null)
				{
					logger.warn("No id provided with event");
					return;
				}
				IPlanElement elem = this.Handle.GetPlan().Locate(ev.GetID());
				if(elem==null)
				{
					logger.warn("No node found with provided id "+ev.GetID());
					return;
				}
				PlanElementPerformanceMetrics pm=new PlanElementPerformanceMetrics();
				pm.Type=elem.GetPlanElementType();
				pm.ChildrenTotalTime=ev.GetChildrenTotalTime();
				pm.FinilizationTime=ev.GetFinilizationTime();
				pm.InitilizationTime=ev.GetInitializationTime();
				pm.CallsNumber=ev.GetSubCalls();
				pm.CallsTotalTime=ev.GetSubCallTotalTime();
				pm.TotalTime=ev.GetTotalTime();
				pm.NumberOfEvents=1;
				synchronized(ExecutionObserver.synchStats)
				{
					if(!ExecutionObserver.PerformanceMetrics.containsKey(pm.Type)) ExecutionObserver.PerformanceMetrics.put(pm.Type, pm);
					else
					{
						PlanElementPerformanceMetrics metr= ExecutionObserver.PerformanceMetrics.get(pm.Type);
						metr.ChildrenTotalTime+=pm.ChildrenTotalTime;
						metr.FinilizationTime+=pm.FinilizationTime;
						metr.InitilizationTime+=pm.InitilizationTime;
						metr.TotalTime+=pm.TotalTime;
						metr.CallsNumber+=pm.CallsNumber;
						metr.CallsTotalTime+=pm.CallsNumber;
						metr.NumberOfEvents+=1;
					}
				}
				break;
			}
			default:
			{
				logger.warn("Received unrecognized event type " + ((ExecutionStateEvent) arg).GetEventName().toString());
			}
		}
	}
}
