package gr.uoa.di.madgik.workflow.adaptor.search.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.workflow.adaptor.search.WorkflowSearchAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.OutputVariableNode;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

public class WorkflowSearchAdaptorTester 
{

	private static Logger logger = LoggerFactory.getLogger(WorkflowSearchAdaptorTester.class);
	
	public static String GetExecutionErrorMessage(ExecutionObserver obs) throws IOException
	{
		StringBuilder buf=new StringBuilder();
		if(obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException) buf.append("Error Cause was "+((ExecutionRunTimeException)obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName());
		StringWriter sw=new StringWriter();
		PrintWriter pw= new PrintWriter(sw);
		obs.GetExecutionHandle().GetCompletionError().printStackTrace(pw);
		pw.flush();
		pw.close();
		sw.flush();
		sw.close();
		sw.toString();
		buf.append(sw.toString());
		return buf.toString();
	}
	
	public static void executionStatus(String executionID) throws Exception
	{
		if(executionID==null || executionID.trim().length()==0) throw new Exception("No execution id provided", new WorkflowValidationException("No execution id provided"));
		ExecutionObserver obs = ExecutionDirectory.Retrieve(executionID);
		if(obs==null) throw new RemoteException("No execution observer found for provided id", new WorkflowValidationException("No execution observer found for provided id"));
		
		boolean completed = obs.IsCompleted();
		String plan = obs.GetExecutionHandle().GetPlan().Serialize();
		
		
		List<ExecutionStateEvent> events= obs.GetEvents();
		if(events.size()==0)
			System.out.println("No new events");
		
		for(ExecutionStateEvent event : events)
		{
			StringBuilder buf=new StringBuilder();
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(event.GetEmitTimestamp());
			SimpleDateFormat dformat=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			buf.append("At "+dformat.format(cal.getTime())+" received "+event.GetEventName());
			
			if(event instanceof ExecutionPerformanceReportStateEvent)
			{
				ExecutionPerformanceReportStateEvent ev = (ExecutionPerformanceReportStateEvent)event;
				buf.append("from element '"+ev.GetID()+"' total time '"+ev.GetTotalTime()+
						"' (init:'"+ev.GetInitializationTime()+"' finalize:'"+ev.GetFinilizationTime()+
						"') children:'"+ev.GetChildrenTotalTime()+"' number of subcalls '"+ev.GetSubCalls()+
						"' with total subcall time '"+ev.GetSubCallTotalTime()+"'");
			}
			if(event instanceof ExecutionProgressReportStateEvent)
			{
				ExecutionProgressReportStateEvent ev = (ExecutionProgressReportStateEvent)event;
				buf.append("from element '"+ev.GetID());
				if(ev.DoesReportProgress()) buf.append(" progress : "+ev.GetCurrentStep()+"/"+ev.GetTotalSteps());
				if(ev.GetMessage()!=null && ev.GetMessage().trim().length()>0) buf.append(" message : "+ev.GetMessage());
			}
			
			if(event instanceof ExecutionExternalProgressReportStateEvent)
			{
				ExecutionExternalProgressReportStateEvent ev = (ExecutionExternalProgressReportStateEvent)event;
				buf.append("from element '"+ev.GetID()+" ("+ev.GetExternalSender()+")");
				if(ev.DoesReportProgress()) buf.append(" progress : "+ev.GetCurrentStep()+"/"+ev.GetTotalSteps());
				if(ev.GetMessage()!=null && ev.GetMessage().trim().length()>0) buf.append(" message : "+ev.GetMessage());
			}
			System.out.println(buf.toString());
		}
		
		
		
		if(completed)
		{
			boolean status = false;
			if(!obs.GetExecutionHandle().IsCompleted())
			{
				logger.info("Execution "+executionID+" Not completed! Why am I here?");
				status = false;
			}
			else if(obs.GetExecutionHandle().IsCompletedWithSuccess())
			{
				logger.info("Execution "+executionID+" successfully completed");
				status =  false;
			}
			else if(obs.GetExecutionHandle().IsCompletedWithError()) 
			{
				String errorString="unsuccessfully completed with error";
				if(obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException) errorString+=" of cause "+((ExecutionRunTimeException)obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName();
				logger.info("Execution "+obs.GetExecutionID()+" "+errorString,obs.GetExecutionHandle().GetCompletionError());
				status =  true;
			}
			else
			{
				logger.info("Execution "+obs.GetExecutionID()+" Completed but neither with success or failure!");
				status = false;
			}
			
			if(status)
			{
				try
				{
					System.out.println("Execution error: " + GetExecutionErrorMessage(obs));
					StringBuilder buf=new StringBuilder();
					for(IPlanElement elem : obs.GetExecutionHandle().GetPlan().LocateActionElements())
					{
						try
						{
							if(elem.GetPlanElementType()!=PlanElementType.Shell) continue;
							if(((ShellPlanElement)elem).StdErrParameter==null) continue;
							Object obj=((ShellPlanElement)elem).StdErrParameter.GetParameterValue(obs.GetExecutionHandle());
							if(obj==null) continue;
							if(!((ShellPlanElement)elem).StdErrIsFile) buf.append(obj.toString());
							else buf.append("Error details located in file stored in Storage System with ID : " + obj.toString());
							buf.append("\n");
						}catch(Exception ex)
						{
							logger.debug("Error trying to retrieve action element error parameter. Continuing");
						}
					}
					System.out.println("Error details: " + buf);
				} catch (IOException ex)
				{
					throw new RemoteException("Could not retrieve error message of unsuccessful execution",ex);
				}
			}
		}
		
	}
	
	private static void constructFiveWayMergePlan(Generator generator) throws Exception 
	{
		MergeSearchPlanGenerator gen = (MergeSearchPlanGenerator)generator;
//		gen.addDataSource("72a7cea0-a7c9-11e0-ae56-935e766b8e8e", 
//				"0caf1204-ff05-4508-9bab-65c3e93da60f geosearch/inclusion=\"1\"/colID=\"cdabe220-a6ff-11e0-9d70-fda94ff03826\"/lang=\"en\" \"10 10 10 1000 1000 1000 1000 10\" "+
//				"project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 79697524-e3bf-457b-891a-faa3a9b0385f");
//		gen.addDataSource("7c23f090-a331-11e0-855d-cb4345c5ce23", 
//				"((((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (((79697524-e3bf-457b-891a-faa3a9b0385f contains cyclospilus) " +
//				"and (79697524-e3bf-457b-891a-faa3a9b0385f contains Pseudotolithus))))) or (((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) " +
//				"and (79697524-e3bf-457b-891a-faa3a9b0385f fuzzy imape)))) project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 79697524-e3bf-457b-891a-faa3a9b0385f");
//		gen.addDataSource("63cf7850-a63a-11e0-98d0-e219cd24d91b", 
//				"((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) " +
//				"project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 79697524-e3bf-457b-891a-faa3a9b0385f");
//		gen.addDataSource("d48100b0-a2fb-11e0-b7e9-abd5e3cba12b", 
//				"((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) " + 
//				"project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 79697524-e3bf-457b-891a-faa3a9b0385f");
//		gen.addDataSource("342dddd0-a63a-11e0-98cf-e219cd24d91b", 
//				"((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f fuzzy imape)) " + 
//				"project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 79697524-e3bf-457b-891a-faa3a9b0385f");
		gen.addDataSource("342dddd0-a63a-11e0-98cf-e219cd24d91b", 
				"((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)) project 79697524-e3bf-457b-891a-faa3a9b0385f");
		gen.addDataSource("c8d900c0-b77b-11e0-be40-da703dc5b4be", 
		"((((gDocCollectionID == \"b4ba3a10-b47d-11e0-9e5d-fda94ff03826\") and (gDocCollectionLang == \"en\"))) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)) project 79697524-e3bf-457b-891a-faa3a9b0385f");
		gen.addDataSource("56b14240-b77c-11e0-be50-da703dc5b4be", 
		"((((gDocCollectionID == \"53321780-ab50-11e0-9da1-fda94ff03826\") and (gDocCollectionLang == \"en\"))) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)) project 79697524-e3bf-457b-891a-faa3a9b0385f");
		gen.addDataSource("dad569f0-b3a5-11e0-9321-cc46719c3f92", 
		"((((gDocCollectionID == \"227b6790-a30d-11e0-91ab-ca34f60d2e2d\") and (gDocCollectionLang == \"en\"))) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)) project 79697524-e3bf-457b-891a-faa3a9b0385");
		gen.addDataSource("7c23f090-a331-11e0-855d-cb4345c5ce23", 
		"((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (0da5e30a-a864-4686-9b5a-307db2c8c8a6 contains map)) project 79697524-e3bf-457b-891a-faa3a9b0385f");
	}
	
	private static void constructThreeWayMergePlan(Generator generator) throws Exception 
	{
        MergeSearchPlanGenerator gen = (MergeSearchPlanGenerator)generator;
		//////////////////////////////////////////////////////////////////////
		/////////Plan 1
		//////////////////////////////////////////////////////////////////////
		gen.addDataSource("001c2f00-abcc-11e0-afbe-c703ad50473c",
				"((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))"); //1
		gen.addDataSource("7c23f090-a331-11e0-855d-cb4345c5ce23", 
				"((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))"); //119
		gen.addDataSource("342dddd0-a63a-11e0-98cf-e219cd24d91b", 
				"((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))"); //4
		
		//String cql = "((((gDocCollectionID == \"3572c6f0-2f5e-11df-a838-c20ddc2e724e\") and (gDocCollectionLang == \"en\"))) and (819777d3-400f-41ee-8e4d-03bcb1e66a1d any map)) project 52b0886f-07e3-43cd-83a9-94cf0d707667 2fa6ce83-2549-4c58-8384-e0b375c9d300";
	
		//////////////////////////////////////////////////////////////////////
		/////////Plan 2
		//////////////////////////////////////////////////////////////////////
//		gen.addDataSource("63cf7850-a63a-11e0-98d0-e219cd24d91b", 
//				"((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687");
//		gen.addDataSource("b3e6e600-abe1-11e0-abe8-a7a4579046dd", 
//				"((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687");
//		gen.addDataSource("d48100b0-a2fb-11e0-b7e9-abd5e3cba12b", 
//				"((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687");
	}
	
	public static void test() throws Exception 
	{
	   TCPConnectionManager.Init(new TCPConnectionManagerConfig("donald.di.uoa.gr", new ArrayList<PortRange>(), true));
	   TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
	   TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		ResourceRegistry.startBridging();
		//Generator gen = new SimpleDatasourceSearchPlanGenerator();
	//	Generator gen = new MergeSearchPlanGenerator(5);
	//	constructFiveWayMergePlan(gen);
		Generator gen = new SimpleMergeSearchPlanGenerator();
		//Generator gen = new SimpleGeoPlanGenerator();
		//Generator gen = new SimpleOpenSearchPlanGenerator();
		//Generator gen = new ThreeWayMergeSearchPlanGenerator();
	//	Generator gen = new JoinOfThreeWayMergesSearchPlanGenerator();
		PlanNode searchPlan = gen.generate();
//		
//		PlanNode searchPlan2 = gen.generate();
//		Set<String> extraInstanceIds = new HashSet<String>();
//		extraInstanceIds.add("foo1");
//		extraInstanceIds.add("foo2");
//		extraInstanceIds.add("foo3");
//		HashMap<String, String> functionalArgs = new HashMap<String, String>();
//		functionalArgs.put("funArg1", "funArgValue1");
//		functionalArgs.put("funArg2", "funArgValue2");
//		((OperatorNode)searchPlan2).addChild(new DataSourceNode(extraInstanceIds, functionalArgs, "CQL1", null));
//		//Collections.shuffle(((OperatorNode)searchPlan2).getChildren());
//		for(PlanNode child1 : ((OperatorNode)searchPlan).getChildren())
//		{
//			System.out.println("Child 1 instance id: " + ((DataSourceNode)child1).getInstanceIds());
//		}
//		for(PlanNode child2 : ((OperatorNode)searchPlan2).getChildren())
//		{
//			System.out.println("Child 2 instance id: " + ((DataSourceNode)child2).getInstanceIds());
//		}
//		
//		System.out.println("Plan1 equals Plan2:" + searchPlan.equals(searchPlan2));
		
		EnvHintCollection adaptorHints = new EnvHintCollection();
		adaptorHints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint("/gcube/devNext")));
		WorkflowSearchAdaptor adaptor = new WorkflowSearchAdaptor(adaptorHints);
		adaptor.SetInputPlan(searchPlan);
		adaptor.CreatePlan();
		ExecutionPlan executionPlan = adaptor.GetCreatedPlan();
		
		String loc = adaptor.ExecutePlan();
		
		System.out.println(executionPlan.Serialize());
		OutputVariableNode outputVariables = adaptor.getOutputVariables();
		String outputVariableName = outputVariables.variableName;
		NamedDataType outputPlanVariable = executionPlan.Variables.Get(outputVariableName);
		DataTypeResultSet outputVariable = (DataTypeResultSet)outputPlanVariable.Value;
		outputVariable.GetValue();
		Object result = ((DataTypeResultSet)executionPlan.Variables.Get(adaptor.getOutputVariables().variableName).Value).GetValue();
		System.out.println(adaptor.GetExecutionID());
		executionStatus(adaptor.GetExecutionID());
		System.out.println(result);
		System.out.println(loc);
		TimeUnit.SECONDS.sleep(10);
		
		//gRSReader.read(new URI(loc));
	}
	
	public static void main(String[] args) throws Exception 
	{
	//	ResourceRegistry.startBridging();
	//	while(!ResourceRegistry.isInitialBridgingComplete()) TimeUnit.SECONDS.sleep(10);
		test();	
	}
}
