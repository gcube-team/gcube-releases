//package gr.uoa.di.madgik.execution.test;
//
//import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
//import gr.uoa.di.madgik.environment.hint.EnvHint;
//import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
//import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
//import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
//import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
//import gr.uoa.di.madgik.execution.exception.ExecutionEngineFullException;
//import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
//import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
//import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
//import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
//import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
//import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
//import gr.uoa.di.madgik.is.InformationSystem;
//import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
//import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory.DirectoryEntryType;
//import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
//import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.nio.ByteBuffer;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.rmi.RemoteException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class SimpleExecutionTest {
//	private static Logger log = LoggerFactory.getLogger(SimpleExecutionTest.class.getName());
//
//	public static void main(String[] args) throws ExecutionSerializationException, ExecutionEngineFullException, ExecutionValidationException,
//			ExecutionInternalErrorException, IOException, EnvironmentValidationException, InterruptedException {
//		final EnvHintCollection Hints = new EnvHintCollection();
//		Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint("/gcube/devsec")));
//		InformationSystem.Init("gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider", Hints);
//
//		String xml = Charset.forName("UTF-8").decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("plan.xml")))).toString();
//
//		xml = xml.replace("$jobId", UUID.randomUUID().toString());
//		
//		System.out.println("Plan:\n" + xml);
//
//		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
//
//		ExecutionPlan plan = new ExecutionPlan();
//		plan.Deserialize(xml);
//
//		ExecutionHandle handle = ExecutionEngine.Submit(plan);
//		Object synchCompletion = new Object();
//		String executionId = ExecutionDirectory.ReserveKey();
//		ExecutionObserver obs = new ExecutionObserver(executionId, DirectoryEntryType.Generic, -1l, handle, null, false, synchCompletion);
//		ExecutionDirectory.Register(obs);
//		handle.RegisterObserver(obs);
//
//		log.info("Executing plan " + executionId);
//		ExecutionEngine.Execute(handle);
//		
//		synchronized (synchCompletion) {
//			while (!obs.IsCompleted()) {
//				try {
//					synchCompletion.wait();
//				} catch (Exception ex) {
//				}
//			}
//		}
//		if (!handle.IsCompleted())
//			log.warn("Not completed! Why am I here?");
//		else if (handle.IsCompletedWithSuccess())
//			log.info("Plan successfully completed");
//		else if (handle.IsCompletedWithError()) {
//			String errorString = "Plan unsuccessfully completed with error";
//			if (handle.GetCompletionError() instanceof ExecutionRunTimeException)
//				errorString += " of cause ";
//			log.info(errorString, handle.GetCompletionError());
//		} else
//			log.warn("Completed but neither with success or failure!");
//
//		log.info("Returning Output Value: " + plan.Variables.ToXML());
//
//		Thread.sleep(10*60*1000);
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
//}
