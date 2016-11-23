package org.gcube.execution.workflowengine.service.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.gcube.execution.workflowengine.service.stubs.ExecutionEvent;
import org.gcube.execution.workflowengine.service.stubs.JobOutput;
import org.gcube.execution.workflowengine.service.stubs.StatusReport;
import org.gcube.execution.workflowengine.service.stubs.StatusRequest;
import org.gcube.execution.workflowengine.service.stubs.WorkflowEngineServicePortType;

public class TestJobStatus extends TestAdaptorBase
{
	private static final long SleepTime=1000*60;
	
	private static void PrintHelp()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Three arguments are needed\n");
		buf.append("1) the path of the file that containing the execution identifier\n");
		buf.append("2) true | false whether the execution plan should be retrieved\n");
		buf.append("3) true | false whether the program should continue monitoring the execution until it is completed\n");
		System.out.println(buf.toString());
	}
	
	public static void main(String []args) throws Exception
	{
		if(args.length!=3)
		{
			TestJobStatus.PrintHelp();
			return;
		}
		boolean includePlan=false;
		try{includePlan=Boolean.parseBoolean(args[1]);}catch(Exception ex){}
		boolean persistStatus=false;
		try{persistStatus=Boolean.parseBoolean(args[2]);}catch(Exception ex){}
		String url=TestAdaptorBase.GetWorkflowEngineURL(args[0]);
		String execid=TestAdaptorBase.GetWorkflowEngineExecutionID(args[0]);
		String scope=TestAdaptorBase.GetWorkflowEngineExecutionScope(args[0]);
		System.out.println("Contacting : "+url);
		System.out.println("Execution identifier : "+execid);
		System.out.println("Scope : "+scope);
		WorkflowEngineServicePortType wf=TestAdaptorBase.GetWorkflowEnginePortType(scope, url);
		
		while(true)
		{
			System.out.println("Sending Request");
			StatusRequest req=new StatusRequest();
			req.setExecutionID(execid);
			req.setIncludePlan(includePlan);
			StatusReport rep=wf.executionStatus(req);
			
			System.out.println("Processing Report");
			if(includePlan || rep.isIsCompleted())
			{
				System.out.println("Written Execution plan to " +TestAdaptorBase.WritePlan(rep.getPlan(), rep.isIsCompleted()));
			}
			
			if(rep.getEvents()!=null)
			{
				for(ExecutionEvent ev : rep.getEvents())
				{
					StringBuilder buf=new StringBuilder();
					Calendar cal= Calendar.getInstance();
					cal.setTimeInMillis(ev.getEventTimestamp());
					SimpleDateFormat dformat=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					buf.append("At "+dformat.format(cal.getTime())+" received "+ev.getEventType());
					
					
					if(ev.getPerformanceEventInfo()!=null)
					{
						buf.append("from element '"+ev.getPerformanceEventInfo().getEmiterID()+"' total time '"+ev.getPerformanceEventInfo().getTotalTime()+
								"' (init:'"+ev.getPerformanceEventInfo().getInitializationTime()+"' finalize:'"+ev.getPerformanceEventInfo().getFinalizationTime()+
								"') children:'"+ev.getPerformanceEventInfo().getChildrenTotalTime()+"' number of subcalls '"+ev.getPerformanceEventInfo().getNumberOfSubcalls()+
								"' with total subcall time '"+ev.getPerformanceEventInfo().getSubcallsTotalTime()+"'");
					}
					if(ev.getProgressEventInfo()!=null)
					{
						buf.append("from element '"+ev.getProgressEventInfo().getEmiterID());
						if(ev.getProgressEventInfo().isReportProgress()) buf.append(" progress : "+ev.getProgressEventInfo().getCurrentStep()+"/"+ev.getProgressEventInfo().getTotalStep());
						if(ev.getProgressEventInfo().isReportNodeProgress()) buf.append(" node : "+ev.getProgressEventInfo().getNodeName()+ " ("+ev.getProgressEventInfo().getNodeHostName()+":"+ev.getProgressEventInfo().getNodePort()+")");
						if(ev.getProgressEventInfo().getMessage()!=null && ev.getProgressEventInfo().getMessage().trim().length()>0) buf.append(" message : "+ev.getProgressEventInfo().getMessage());
					}
					if(ev.getProgressExternalEventInfo()!=null)
					{
						buf.append("from element '"+ev.getProgressExternalEventInfo().getEmiterID()+" ("+ev.getProgressExternalEventInfo().getExternalEmiterName()+")");
						if(ev.getProgressExternalEventInfo().isReportProgress()) buf.append(" progress : "+ev.getProgressExternalEventInfo().getCurrentStep()+"/"+ev.getProgressExternalEventInfo().getTotalStep());
						if(ev.getProgressExternalEventInfo().getMessage()!=null && ev.getProgressExternalEventInfo().getMessage().trim().length()>0) buf.append(" message : "+ev.getProgressExternalEventInfo().getMessage());
					}
					System.out.println(buf.toString());
				}
			}
			else
			{
				System.out.println("No new events");
			}
			
			if(rep.isIsCompleted())
			{
				System.out.println("Execution has completed");
				if(rep.getError()!=null && rep.getError().trim().length()>0)
				{
					System.out.println("Error reported : \n"+rep.getError());
					System.out.println("Error details Retrieved : \n"+rep.getErrorDetails());
				}
				if(rep.getOutput()!=null)
				{
					System.out.println("Job output :");
					for(JobOutput jo : rep.getOutput())
					{
						StringBuilder buf=new StringBuilder();
						if(jo.getKey()!=null && jo.getKey().trim().length()>0) buf.append("key : "+jo.getKey());
						else buf.append("key : Not available");
						if(jo.getSubKey()!=null && jo.getSubKey().trim().length()>0) buf.append(" subkey : "+jo.getSubKey());
						else buf.append(" subkey : Not available");
						if(jo.getStorageSystemID()!=null && jo.getStorageSystemID().trim().length()>0) buf.append(" StorageSystem ID : "+jo.getStorageSystemID());
						else  buf.append(" StorageSystem ID : Not available");
						System.out.println(buf.toString());
					}
				}
			}
			else
			{
				System.out.println("Execution has not yet completed");
			}
			if(persistStatus)
			{
				if(rep.isIsCompleted()) break;
				includePlan=false;
				System.out.println("Sleeping for "+(TestJobStatus.SleepTime/1000)+" seconds");
				try{Thread.sleep(TestJobStatus.SleepTime);}catch(Exception ex){}
			}
			else
			{
				break;
			}
		}
	}
}
