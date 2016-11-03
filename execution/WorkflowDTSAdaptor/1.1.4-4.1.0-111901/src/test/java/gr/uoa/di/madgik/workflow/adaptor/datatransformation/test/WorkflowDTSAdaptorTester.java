//package gr.uoa.di.madgik.workflow.adaptor.datatransformation.test;
//
//import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
//import gr.uoa.di.madgik.commons.server.PortRange;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
//import gr.uoa.di.madgik.environment.hint.EnvHint;
//import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
//import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
//import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
//import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
//import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
//import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
//import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
//import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
//import gr.uoa.di.madgik.is.InformationSystem;
//import gr.uoa.di.madgik.rr.ResourceRegistry;
//import gr.uoa.di.madgik.workflow.adaptor.datatransformation.WorkflowDTSAdaptor;
//import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
//import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
//import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.net.InetAddress;
//import java.rmi.RemoteException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import org.gcube.datatransformation.datatransformationlibrary.ProgramExecutor;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
//import org.gcube.datatransformation.datatransformationlibrary.imanagers.LocalInfoManager;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
//import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraphImpl;
//import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author john.gerbesiotis - DI NKUA
// * 
// */
//public class WorkflowDTSAdaptorTester {
//	private static Logger log = LoggerFactory.getLogger(WorkflowDTSAdaptorTester.class.getName());
//
//	public static String GetExecutionErrorMessage(ExecutionObserver obs) throws IOException {
//		StringBuilder buf = new StringBuilder();
//		if (obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException)
//			buf.append("Error Cause was " + ((ExecutionRunTimeException) obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName());
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		obs.GetExecutionHandle().GetCompletionError().printStackTrace(pw);
//		pw.flush();
//		pw.close();
//		sw.flush();
//		sw.close();
//		sw.toString();
//		buf.append(sw.toString());
//		return buf.toString();
//	}
//
//	public static void executionStatus(String executionID) throws Exception {
//		if (executionID == null || executionID.trim().length() == 0)
//			throw new Exception("No execution id provided", new WorkflowValidationException("No execution id provided"));
//		ExecutionObserver obs = ExecutionDirectory.Retrieve(executionID);
//		if (obs == null)
//			throw new RemoteException("No execution observer found for provided id", new WorkflowValidationException(
//					"No execution observer found for provided id"));
//
//		boolean completed = obs.IsCompleted();
//		String plan = obs.GetExecutionHandle().GetPlan().Serialize();
//
//		List<ExecutionStateEvent> events = obs.GetEvents();
//		if (events.size() == 0)
//			System.out.println("No new events");
//
//		for (ExecutionStateEvent event : events) {
//			StringBuilder buf = new StringBuilder();
//			Calendar cal = Calendar.getInstance();
//			cal.setTimeInMillis(event.GetEmitTimestamp());
//			SimpleDateFormat dformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
//			buf.append("At " + dformat.format(cal.getTime()) + " received " + event.GetEventName());
//
//			if (event instanceof ExecutionPerformanceReportStateEvent) {
//				ExecutionPerformanceReportStateEvent ev = (ExecutionPerformanceReportStateEvent) event;
//				buf.append("from element '" + ev.GetID() + "' total time '" + ev.GetTotalTime() + "' (init:'" + ev.GetInitializationTime() + "' finalize:'"
//						+ ev.GetFinilizationTime() + "') children:'" + ev.GetChildrenTotalTime() + "' number of subcalls '" + ev.GetSubCalls()
//						+ "' with total subcall time '" + ev.GetSubCallTotalTime() + "'");
//			}
//			if (event instanceof ExecutionProgressReportStateEvent) {
//				ExecutionProgressReportStateEvent ev = (ExecutionProgressReportStateEvent) event;
//				buf.append("from element '" + ev.GetID() + " node: " + ev.GetNodeHostName());
//				if (ev.DoesReportProgress())
//					buf.append(" progress : " + ev.GetCurrentStep() + "/" + ev.GetTotalSteps());
//				if (ev.GetMessage() != null && ev.GetMessage().trim().length() > 0)
//					buf.append(" message : " + ev.GetMessage());
//			}
//
//			if (event instanceof ExecutionExternalProgressReportStateEvent) {
//				ExecutionExternalProgressReportStateEvent ev = (ExecutionExternalProgressReportStateEvent) event;
//				buf.append("from element '" + ev.GetID() + " (" + ev.GetExternalSender() + ")");
//				if (ev.DoesReportProgress())
//					buf.append(" progress : " + ev.GetCurrentStep() + "/" + ev.GetTotalSteps());
//				if (ev.GetMessage() != null && ev.GetMessage().trim().length() > 0)
//					buf.append(" message : " + ev.GetMessage());
//			}
//			System.out.println(buf.toString());
//		}
//
//		if (completed) {
//			boolean status = false;
//			if (!obs.GetExecutionHandle().IsCompleted()) {
//				log.info("Execution " + executionID + " Not completed! Why am I here?");
//				status = false;
//			} else if (obs.GetExecutionHandle().IsCompletedWithSuccess()) {
//				log.info("Execution " + executionID + " successfully completed");
//				status = false;
//			} else if (obs.GetExecutionHandle().IsCompletedWithError()) {
//				String errorString = "unsuccessfully completed with error";
//				if (obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException)
//					errorString += " of cause " + ((ExecutionRunTimeException) obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName();
//				log.info("Execution " + obs.GetExecutionID() + " " + errorString, obs.GetExecutionHandle().GetCompletionError());
//				status = true;
//			} else {
//				log.info("Execution " + obs.GetExecutionID() + " Completed but neither with success or failure!");
//				status = false;
//			}
//
//			if (status) {
//				try {
//					System.out.println("Execution error: " + GetExecutionErrorMessage(obs));
//					StringBuilder buf = new StringBuilder();
//					for (IPlanElement elem : obs.GetExecutionHandle().GetPlan().LocateActionElements()) {
//						try {
//							if (elem.GetPlanElementType() != PlanElementType.Shell)
//								continue;
//							if (((ShellPlanElement) elem).StdErrParameter == null)
//								continue;
//							Object obj = ((ShellPlanElement) elem).StdErrParameter.GetParameterValue(obs.GetExecutionHandle());
//							if (obj == null)
//								continue;
//							if (!((ShellPlanElement) elem).StdErrIsFile)
//								buf.append(obj.toString());
//							else
//								buf.append("Error details located in file stored in Storage System with ID : " + obj.toString());
//							buf.append("\n");
//						} catch (Exception ex) {
//							log.trace("Error trying to retrieve action element error parameter. Continuing");
//						}
//					}
//					System.out.println("Error details: " + buf);
//				} catch (IOException ex) {
//					throw new RemoteException("Could not retrieve error message of unsuccessful execution", ex);
//				}
//			}
//		}
//
//	}
//
//	public static void test() throws Exception {
////		List<PortRange> ports=new ArrayList<PortRange>(); //The ports that the TCPConnection manager should use
////		ports.add(new PortRange(6050, 6100));         
////		TCPConnectionManager.Init(
////				new TCPConnectionManagerConfig(InetAddress.getLocalHost().getHostName(), //The hostname by which the machine is reachable 
////						ports,                                    //The ports that can be used by the connection manager
////						true                                      //If no port ranges were provided, or none of them could be used, use a random available port
////				));
//
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//
//		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//
//		ResourceRegistry.startBridging();
//		
//		while(!ResourceRegistry.isInitialBridgingComplete()){
//			System.out.println("waiting...");
//			Thread.sleep(5000);
//		}
//		
//		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
//
//		LocalInfoManager imanager = new LocalInfoManager();
//		imanager.setProgramsFile("src/main/resources/programs.xml");
//		try {
//			ProgramExecutor.initializeDeployer("dts_libs_path");
//		} catch (Exception e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//		}
//
//		String scope = "/gcube/devNext";		
//		String providerInformationName = "gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";
//		
//		EnvHintCollection hints = new EnvHintCollection();
//		hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));
//		
//		InformationSystem.Init(providerInformationName, new EnvHintCollection());
//
//		TransformationsGraphImpl graph = new TransformationsGraphImpl(imanager);
//		
//		String inputType = "Local";
//		String inputValue = "/home/jgerbe/testArea/src4";
//		Input input = new Input(inputType, inputValue, null);
//		
//		String outputType = "Local";
//		String outputValue = "/home/jgerbe/testArea/dest";
//		Output output = new Output(outputType, outputValue, null);
//
//		WorkflowDTSAdaptor adaptor = new WorkflowDTSAdaptor();
//		TransformationDescription desc = new TransformationDescription(input, output, false);
//		
//		adaptor.setTransPlan(desc);
//		adaptor.SetScope(scope);
//		adaptor.setRequirements("dts.execution==true");
//		adaptor.ConstructEnvironmentHints();
//		adaptor.CreatePlan();
//		ExecutionPlan executionPlan = adaptor.GetCreatedPlan();
//
//		System.out.println(executionPlan.Serialize());
//
//		adaptor.ExecutePlan();
//		
//		ContentType tiffCT = new ContentType("image/tiff",  new ArrayList<Parameter>());
//		ContentType jpegCT = new ContentType("image/jpeg",  new ArrayList<Parameter>());
//		ContentType gifCT = new ContentType("image/gif",  new ArrayList<Parameter>());
//		ContentType pngCT = new ContentType("image/png",  new ArrayList<Parameter>());
//		{
//			ArrayList<TransformationUnit> tus = new ArrayList<TransformationUnit>();
//			ArrayList<ContentType> cts = new ArrayList<ContentType>();
//			
//			tus.add(graph.findApplicableTransformationUnits(tiffCT, pngCT, true).get(0));
//			cts.add(pngCT);
//			
//			tus.add(graph.findApplicableTransformationUnits(pngCT, gifCT, true).get(0));
//			cts.add(gifCT);
//
////			TransformationUnitConverter conv = new TransformationUnitConverter();
////			System.out.println(conv.Convert(tus.get(0)));
//			adaptor.addPlan(tus, cts);
//		}
////		{
////			ArrayList<TransformationUnit> tus = new ArrayList<TransformationUnit>();
////			ArrayList<ContentType> cts = new ArrayList<ContentType>();
////			
////			tus.add(graph.findApplicableTransformationUnits(jpegCT, pngCT, true).get(0));
////			cts.add(pngCT);
////			
//////			TransformationUnitConverter conv = new TransformationUnitConverter();
//////			System.out.println(conv.Convert(tus.get(0)));
////			adaptor.addPlan(tus, cts);
////		}
////		{
////			ArrayList<TransformationUnit> tus = new ArrayList<TransformationUnit>();
////			ArrayList<ContentType> cts = new ArrayList<ContentType>();
////			
////			tus.add(graph.findApplicableTransformationUnits(gifCT, pngCT, true).get(0));
////			cts.add(pngCT);
////			
//////			TransformationUnitConverter conv = new TransformationUnitConverter();
//////			System.out.println(conv.Convert(tus.get(0)));
////			adaptor.addPlan(tus, cts);
////		}
//		
////		Thread.sleep(5000);
//		System.out.println(adaptor.GetExecutionID());
//		executionStatus(adaptor.GetExecutionID());
//
//		Thread.sleep(60000);
//		adaptor.finishedAddingPLans();
//		
//		Thread.sleep(60000);
//		
////		OutputVariableNode outputVariables = adaptor.getOutputVariables();
////		String outputVariableName = outputVariables.variableName;
////		NamedDataType outputPlanVariable = executionPlan.Variables.Get(outputVariableName);
////		DataTypeResultSet outputVariable = (DataTypeResultSet) outputPlanVariable.Value;
////		outputVariable.GetValue();
////		Object result = ((DataTypeResultSet) executionPlan.Variables.Get(adaptor.getOutputVariables().variableName).Value).GetValue();
////		System.out.println(result);
////		System.out.println(loc);
//	}
//
//	public static void main(String[] args) throws Exception {
//		test();
//	}
//}