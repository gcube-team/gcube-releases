package gr.uoa.di.madgik.workflow.reporter;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.reporting.ReportingFramework;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;

public class ExecutionReporter 
{
	
	private static Logger logger = LoggerFactory.getLogger(ExecutionReporter.class);
	
	private ExecutionReporter() { }
	
	public static void ReportExecutionStatus(ExecutionStateEvent event, String ExecutionID, ExecutionDirectory.DirectoryEntryType type, EnvHintCollection hints)
	{	
		try 
		{
			Map<String, Object> reportingParams = new HashMap<String, Object>();
			reportingParams.put("ExecutionID", ExecutionID);
			reportingParams.put("Type", type.toString());
			reportingParams.put("Timestamp", event.GetEmitTimestamp());
			PopulateMessageParameters(event, reportingParams);
			
			ReportingFramework.Send(event.GetEventName().toString(), reportingParams, hints);
		}catch(EnvironmentReportingException e)
		{
			logger.warn("Could not report " + event.GetEventName() + " event", e);
		}
	}
	
	private static void PopulateMessageParameters(ExecutionStateEvent event, Map<String, Object> reportingParams)
	{
		switch (event.GetEventName())
		{
			case ExecutionCompleted:
			case ExecutionCancel:
			case ExecutionPause:
			case ExecutionResume:
			case ExecutionStarted:
				break;
			case ExecutionPerformance:
			{
				ExecutionPerformanceReportStateEvent ev = (ExecutionPerformanceReportStateEvent)event;
				reportingParams.put("InitializationTime", ev.GetInitializationTime());
				reportingParams.put("SubCalls", ev.GetSubCalls());
				reportingParams.put("SubCallsTotalTime", ev.GetSubCallTotalTime());
				reportingParams.put("ChildrenTotalTime", ev.GetChildrenTotalTime());
				reportingParams.put("FinalizationTime", ev.GetFinilizationTime());
				reportingParams.put("TotalTime", ev.GetTotalTime());
				break;
			}
			case ExecutionExternalProgress:
			{
				ExecutionExternalProgressReportStateEvent ev = (ExecutionExternalProgressReportStateEvent)event;
				if(ev.DoesReportProgress()) 
					reportingParams.put("DoesReportProgress", 1);
				else
					reportingParams.put("DoesReportProgress", 0);
				reportingParams.put("CurrentStep", ev.GetCurrentStep());
				reportingParams.put("TotalSteps", ev.GetTotalSteps());
				reportingParams.put("ExternalSender", ev.GetExternalSender());
				reportingParams.put("Message", ev.GetMessage());
				break;
			}
			case ExecutionProgress:
			{
				ExecutionProgressReportStateEvent ev = (ExecutionProgressReportStateEvent)event;
				if(ev.DoesReportProgress()) 
					reportingParams.put("DoesReportProgress", 1);
				else
					reportingParams.put("DoesReportProgress", 0);
				reportingParams.put("CurrentStep", ev.GetCurrentStep());
				reportingParams.put("TotalSteps", ev.GetTotalSteps());
				reportingParams.put("Message", ev.GetMessage());
				break;
			}
		}
	}
}

