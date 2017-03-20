package gr.uoa.di.madgik.workflow.adaptor.datatransformation.test;
//
//import gr.uoa.di.madgik.commons.server.PortRange;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
//import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
//import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
//import gr.uoa.di.madgik.execution.exception.ExecutionException;
//import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
//import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
//import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
//import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
//import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
//import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
//import gr.uoa.di.madgik.grs.record.GenericRecord;
//import gr.uoa.di.madgik.grs.record.field.Field;
//import gr.uoa.di.madgik.grs.record.field.StringField;
//import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
//import gr.uoa.di.madgik.workflow.adaptor.datatransformation.WorkflowDTSSubplanAdaptor;
//import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.TransformationUnitConverter;
//import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
//import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
//import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.rmi.RemoteException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
//import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
//
public class WorkflowDTSSubplanAdaptorTester {}
//	private Logger log = LoggerFactory.getLogger(WorkflowDTSSubplanAdaptorTester.class);
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
//				buf.append("from element '" + ev.GetID());
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
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//
//		// LocalInfoManager imanager = new LocalInfoManager();
//		// imanager.setProgramsFile("programs.xml");
//		// try {
//		// ProgramExecutor.initializeDeployer("dts_libs_path");
//		// } catch (Exception e3) {
//		// // TODO Auto-generated catch block
//		// e3.printStackTrace();
//		// }
//		// TransformationsGraphImpl graph = new
//		// TransformationsGraphImpl(imanager);
//
//		String inputType = "Local";
//		String inputValue = "/home/jgerbe/src";
//		Input input = new Input(inputType, inputValue, null);
//
//		String outputType = "Local";
//		String outputValue = "/home/jgerbe/dest";
//		Output output = new Output(outputType, outputValue, null);
//
//		ContentType sourceContentType = new ContentType("image/jpeg", null);
//		ContentType targetContentType = new ContentType("image/png", null);
//
//		ArrayList<Input> inputs = new ArrayList<Input>();
//		inputs.add(input);
//		TransformationDescription desc = new TransformationDescription(inputs, output);
//
//		String s = "<Resource><TransformationUnit id=\"0\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/jpeg</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters><Parameter isOptional=\"false\" name=\"method\" value=\"convert\"/></ProgramParameters><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/png</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target><TransformationPrograms><gDTSTransformationProgram><ID>2222222</ID><Type/><Profile><SecondaryType>DTSTransformationProgram</SecondaryType><Name>gDTSTP_ImageConverter</Name><Description>Program transforming images</Description><Body><gDTSTransformationProgram><Transformer><Software/><Class>org.gcube.datatransformation.datatransformationlibrary.programs.images.ImageMagickWrapper</Class><GlobalProgramParameters/></Transformer><TransformationUnits><TransformationUnit id=\"0\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/jpeg</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters><Parameter isOptional=\"false\" name=\"method\" value=\"convert\"/></ProgramParameters><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/png</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit><TransformationUnit id=\"1\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/jpg</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters/><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/gif</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit><TransformationUnit id=\"2\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/png</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters/><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/jpg</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit><TransformationUnit id=\"3\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/png</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters/><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/gif</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit><TransformationUnit id=\"4\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/gif</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters/><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/jpg</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit><TransformationUnit id=\"5\" isComposite=\"false\"><Sources><Source><Input id=\"TRInput0\"/><ContentType><Mimetype>image/gif</Mimetype><Parameters/></ContentType></Source></Sources><ProgramParameters/><Target><Output id=\"TROutput\"/><ContentType><Mimetype>image/png</Mimetype><Parameters><Parameter isOptional=\"true\" name=\"width\" value=\"*\"/><Parameter isOptional=\"true\" name=\"height\" value=\"*\"/></Parameters></ContentType></Target></TransformationUnit></TransformationUnits></gDTSTransformationProgram></Body></Profile></gDTSTransformationProgram></TransformationPrograms></TransformationUnit></Resource>";
//
//		TransformationUnitConverter conv = new TransformationUnitConverter();
//		ArrayList<TransformationUnit> tus = new ArrayList<TransformationUnit>();
//		ArrayList<String> mimes = new ArrayList<String>();
//		mimes.add(targetContentType.getMimeType());
//		
//		tus.add((TransformationUnit) conv.Convert(s));
//		desc.add(tus, mimes);
//
//		WorkflowDTSSubplanAdaptor subPlan = null;
//		try {
//			subPlan = new WorkflowDTSSubplanAdaptor();
//		} catch (Exception e) {
//			log.error("Subplan addition initialization failed.");
//		}
//
//		subPlan.SetScope("/gcube/devNext");
//		subPlan.setTransPlan(desc);
//
//		subPlan.CreatePlan();
//		String locator = null;
//		try {
//			locator = subPlan.ExecutePlan();
//		} catch (ExecutionException e) {
//			log.error("Subplan addition execution failed.");
//			throw new Exception(e);
//		}
//
//		ExecutionPlan executionPlan = subPlan.GetCreatedPlan();
//
//		System.out.println(executionPlan.Serialize());
//
//
//		// ArrayList<TransformationUnit> tus =
//		// graph.findApplicableTransformationUnits(new ContentType("image/jpeg",
//		// new ArrayList<Parameter>()), new ContentType("image/png", new
//		// ArrayList<Parameter>()), false);
//
//		// Thread.sleep(5000);
//		System.out.println(subPlan.GetExecutionID());
//		executionStatus(subPlan.GetExecutionID());
//
//		// adaptor.put(uri);
//		// Thread.sleep(2000);
//
//		Thread.sleep(10000);
//
//		// OutputVariableNode outputVariables = adaptor.getOutputVariables();
//		// String outputVariableName = outputVariables.variableName;
//		// NamedDataType outputPlanVariable =
//		// executionPlan.Variables.Get(outputVariableName);
//		// DataTypeResultSet outputVariable = (DataTypeResultSet)
//		// outputPlanVariable.Value;
//		// outputVariable.GetValue();
//		// Object result = ((DataTypeResultSet)
//		// executionPlan.Variables.Get(adaptor.getOutputVariables().variableName).Value).GetValue();
//		// System.out.println(result);
//		// System.out.println(loc);
//	}
//
//	public static void main(String[] args) throws Exception {
//		test();
//	}
//}
