package gr.uoa.di.madgik.workflow.adaptor.datatransformation;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.nodeselection.NodePicker;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.OutputVariableNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.ContentTypeConverter;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.InputConverter;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.TransformationUnitConverter;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors.DataSourceElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors.ElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors.TransformationElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.FunctionalityWrapper;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory.DirectoryEntryType;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workflow adaptor used in data transformation process.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class WorkflowDTSSubplanAdaptor implements IWorkflowAdaptor {
	/** The logger. */
	private Logger log = LoggerFactory.getLogger(WorkflowDTSSubplanAdaptor.class.getName());

	/** Running scope. */
	private String scope = null;

	/** The transformation unit from which an execution plan will be created. */
	private TransformationDescription transDesc = null;

	/** The constructed execution plan. */
	private ExecutionPlan plan = null;

	/** The id of execution plan. */
	private String executionId = null;

	private WrapperNode wrapperTree = null;

	private OutputVariableNode ioVariableNodes = null;

	private ExecutionHandle handle = null;

	/**
	 * The node selector which will be used to select execution nodes that will
	 * be used.
	 */
	private NodePicker nodePicker;

	/** Environment hint collection */
	private EnvHintCollection Hints=new EnvHintCollection();

	private boolean isLocal = true;
	
	public WorkflowDTSSubplanAdaptor(NodePicker nodePicker, boolean local) throws Exception {
		this(nodePicker);
		this.isLocal = local;
	}

	public WorkflowDTSSubplanAdaptor(NodePicker nodePicker) throws Exception {
		plan = new ExecutionPlan();
		plan.Config = new PlanConfig();
		this.nodePicker = nodePicker; 
		
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
	}

	public void setTransPlan(TransformationDescription desc) {
		transDesc = desc;
	}

	public void SetEnv(EnvHintCollection hints) {
		Hints = hints;
		
		this.scope = hints.GetHint("GCubeActionScope").Hint.Payload;
	}

	public void SetExecutionId(String executionId) {
		this.executionId = executionId;
	}

	@Override 
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException,WorkflowInternalErrorException, WorkflowEnvironmentException {
		if (this.scope == null) {
			throw new WorkflowValidationException("No scope specified");
		}
		if (transDesc == null) {
			throw new WorkflowValidationException("No transformation description specified");
		}

		NodeExecutionInfo planInfo = null;

		try {
			planInfo = ConstructWorkflow();
//			((ProcessingWrapper) planInfo.wrapperNode.wrapper).elevate();
//			((SequencePlanElement)planInfo.element).ElementCollection.add(((ProcessingWrapper)planInfo.wrapperNode.wrapper).getElevationElement());
//			plan.Variables.Add(((ProcessingWrapper) planInfo.wrapperNode.wrapper).getElevationVariable());
		} catch (Exception e) {
			log.error("Could not construct workflow", e);
			throw new WorkflowInternalErrorException("Could not construct workflow");
		}
		plan.Root = planInfo.element;
		wrapperTree = planInfo.wrapperNode;
		plan.EnvHints = this.Hints;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetCreatedPlan()
	 */
	public ExecutionPlan GetCreatedPlan() {
		return plan;
	}

	public String GetExecutionID() {
		return executionId;
	}

	public String ExecutePlan() throws Exception {
		if (plan.Root == null)
			throw new WorkflowValidationException("No execution plan has been created");
		if (scope == null)
			throw new WorkflowValidationException("No scope specified");

		handle = ExecutionEngine.Submit(plan);
		Object synchCompletion = new Object();
		executionId = ExecutionDirectory.ReserveKey();
		ExecutionObserver obs = new ExecutionObserver(executionId, DirectoryEntryType.Generic, -1l, handle, this, false, synchCompletion);
		ExecutionDirectory.Register(obs);
		handle.RegisterObserver(obs);

		log.info("Executing plan " + executionId);
		ExecutionEngine.Execute(handle);
		synchronized (synchCompletion) {
			while (!obs.IsCompleted()) {
				try {
					synchCompletion.wait();
				} catch (Exception ex) {
				}
			}
		}
		String retVal = null;
		if (!handle.IsCompleted())
			log.warn("Not completed! Why am I here?");
		else if (handle.IsCompletedWithSuccess()){
			log.info("Plan successfully completed");
			retVal = this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue();
		}
		else if (handle.IsCompletedWithError()) {
			String errorString = "Plan unsuccessfully completed with error";
			Throwable e = handle.GetCompletionError().getCause();
			while (e instanceof ExecutionException || e instanceof ExecutionRunTimeException) {
				e = e.getCause();
			}
			log.info(errorString, e);
			throw new Exception(e);
		} else
			log.warn("Completed but neither with success or failure!");

		log.info("Returning grs2 locator: " + this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue());

		return retVal;
	}

	public ExecutionException GetCompletionError() throws WorkflowValidationException {
		if (plan == null)
			throw new WorkflowValidationException("No execution plan has been created");
		return handle.GetCompletionError();
	}

	// Boundaries
	//	Seq{
	//		Bound{
	//			seq{DS,A}
	//		},
	// 		Bound{B},
	// 		Bound{C}
	//	 }
	//	}
	private NodeExecutionInfo ConstructWorkflow() throws Exception {
		if (!transDesc.hasMorePlansToBeAdded()) {
			throw new WorkflowValidationException("No more plans to be added");
		}

		SequencePlanElement seq = new SequencePlanElement();

		int numOfPlansAdded = transDesc.getNumOfPlansAdded();
		ArrayList<TransformationUnit> tPath = transDesc.getTransformationPath(numOfPlansAdded);
		ArrayList<ContentType> contentTypes = transDesc.getContentTypes(numOfPlansAdded);
		transDesc.setNumOfPlansAdded(++numOfPlansAdded);

		ElementConstructor dataSourceElementConstructor = new DataSourceElementConstructor();

		// Wrap input inside a DataTypeConvertable
		DataTypeConvertable inputDS = new DataTypeConvertable();
		inputDS.SetConverter(InputConverter.class.getName());
		inputDS.SetValue(transDesc.getInput());

		NamedDataType ndtInputDS = new NamedDataType();
		ndtInputDS.Value = inputDS;

		// Set arguments for DataSourceElementConstructor. Set desired source's
		ContentType ct;
		if (tPath.isEmpty()){ // When source and target have same content type
			ct = contentTypes.get(0);
		}else {
			ct = new ContentType(tPath.get(0).getSources().get(0).getContentType().getMimeType(), null);
		}
		DataTypeConvertable contentTypeDTC = new DataTypeConvertable();
		contentTypeDTC.SetConverter(ContentTypeConverter.class.getName());
		contentTypeDTC.SetValue(ct);
		
		NamedDataType ndtContentType = new NamedDataType();
		ndtContentType.Value = contentTypeDTC;

		NodeExecutionInfo dataSourcePlanElement = dataSourceElementConstructor.contructPlanElement(null, new NamedDataType[] { ndtInputDS, ndtContentType });

		FunctionalityWrapper wrapper = null;
		wrapper = dataSourcePlanElement.wrapperNode.wrapper;
		wrapper.addVariablesToPlan(this.plan);

		
		SequencePlanElement innerSeq = new SequencePlanElement();
		IPlanElement firstBound = ConstructBoundaryElement(nodePicker.selectDataSourceExecutionNode(), innerSeq);

		innerSeq.ElementCollection.add(dataSourcePlanElement.element);
		
		seq.ElementCollection.add(firstBound);

		NamedDataType inputToNext = dataSourcePlanElement.wrapperNode.wrapper.getOutputVariable(); 

		NodeExecutionInfo nextPlanElement = dataSourcePlanElement;
		int i = 0;
		for (TransformationUnit tUnit : tPath) {
			ElementConstructor transformationElementConstructor = new TransformationElementConstructor();

			DataTypeConvertable tUnitDTC = new DataTypeConvertable();
			tUnitDTC.SetConverter(TransformationUnitConverter.class.getName());
			tUnitDTC.SetValue(tUnit);

			NamedDataType ndtTUnit = new NamedDataType();
			ndtTUnit.Value = tUnitDTC;

			DataTypeConvertable targetContentTypeDTC = new DataTypeConvertable();
			targetContentTypeDTC.SetConverter(ContentTypeConverter.class.getName());
			targetContentTypeDTC.SetValue(contentTypes.get(i++));
			
			NamedDataType ndtTargetContentType = new NamedDataType();
			ndtTargetContentType.Value = targetContentTypeDTC;
			
			DataTypeString scopeDTC = new DataTypeString();
			scopeDTC.SetStringValue(scope);
			NamedDataType ndtScope = new NamedDataType();
			ndtScope.Value = scopeDTC;
			
			nextPlanElement = transformationElementConstructor.contructPlanElement(null, new NamedDataType[] { inputToNext, ndtTUnit, ndtTargetContentType, ndtScope });
			
			wrapper = nextPlanElement.wrapperNode.wrapper;
			wrapper.addVariablesToPlan(this.plan);
			
			if ( i == 1) // first transform
				innerSeq.ElementCollection.add(nextPlanElement.element);
			else {
				IPlanElement bound = ConstructBoundaryElement(nodePicker.selectTransformationExecutionNode(), nextPlanElement.element);
				seq.ElementCollection.add(bound);
			}

			inputToNext = nextPlanElement.wrapperNode.wrapper.getOutputVariable();
		}

		return new NodeExecutionInfo(seq, nextPlanElement.wrapperNode);
	}

	private IPlanElement ConstructBoundaryElement(String hostingNode, IPlanElement root) throws WorkflowEnvironmentException {
		if (isLocal)
			return root;
		
		BoundaryPlanElement bound = new BoundaryPlanElement();
		bound.CleanUpLocalFiles.clear();
		bound.Triggers.clear();
		bound.Attachments.clear();
		bound.Config = GetBoundaryConfig(hostingNode);
		bound.Isolation = new BoundaryIsolationInfo();
		bound.Isolation.Isolate = true;
		bound.Isolation.CleanUp = true;
		bound.Isolation.BaseDir = new SimpleInOutParameter();
		NamedDataType ndtIsolationBaseDirParameter = new NamedDataType();
		ndtIsolationBaseDirParameter.IsAvailable = false;
		ndtIsolationBaseDirParameter.Name = UUID.randomUUID().toString();
		ndtIsolationBaseDirParameter.Token = ndtIsolationBaseDirParameter.Name;
		ndtIsolationBaseDirParameter.Value = new DataTypeString();
		plan.Variables.Add(ndtIsolationBaseDirParameter);
		((SimpleInOutParameter) bound.Isolation.BaseDir).VariableName = ndtIsolationBaseDirParameter.Name;
		
		bound.Root = root;
		
		return bound;
	}

	private BoundaryConfig GetBoundaryConfig(String hostingNode) throws WorkflowEnvironmentException {
		if (hostingNode == null || hostingNode.contains("null"))
			throw new WorkflowEnvironmentException("Could not find appopriate node for transformation");
		
		BoundaryConfig Config = new BoundaryConfig();
		Config.HostName = hostingNode.split(":")[0];
		Config.Port = Integer.parseInt(hostingNode.split(":")[1]);
		Config.NozzleConfig = new TCPServerNozzleConfig(false, 0);
		return Config;
	}

	@Override
	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<IOutputResource> GetOutput() {
		// TODO Auto-generated method stub
		return null;
	}

//	public static void main(String args[]) throws Exception {
//		System.out.println("helloha!");
//		DataTypeConvertable dtc = new DataTypeConvertable();
//
//		dtc.SetConverter(InputConverter.class.getName());
//		Input input = new Input();
//
//		input.setInputType("FTP");
//		input.setInputValue("ftp://meteora.di.uoa.gr");
//		input.setInputparameters(new Parameter[] { new Parameter("username", "giannis"), new Parameter("password", "ftpsketo") });
//		dtc.SetValue(input);
//
//		System.out.println(dtc.GetConvertedValue());
//		System.out.println(dtc.GetStringValue());
//
//		Input in2 = (Input) new InputConverter().Convert(dtc.GetConvertedValue());
//
//		DataTypeConvertable dtc2 = new DataTypeConvertable();
//		dtc2.SetConverter(InputConverter.class.getName());
//		dtc2.SetValue(in2);
//		System.out.println("\n" + dtc2.GetStringValue());
//	}
}
