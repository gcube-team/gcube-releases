package gr.uoa.di.madgik.workflow.adaptor.datatransformation;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
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
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge.WriterHolder;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.nodeselection.NodePicker;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters.OutputConverter;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors.ElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.elementconstructors.MergeElementConstructor;
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gcube.datatransformation.datatransformationlibrary.adaptor.DTSAdaptor;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workflow adaptor used in data transformation process, for full
 * transformations from one type to another.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class WorkflowDTSAdaptor implements IWorkflowAdaptor, DTSAdaptor {
	/** The logger. */
	private Logger log = LoggerFactory.getLogger(WorkflowDTSAdaptor.class.getName());

	/** Running scope. */
	private String scope = null;

	/** The transformation unit from which an execution plan will be created. */
	private TransformationDescription transDesc = null;

	/** The constructed execution plan. */
	private ExecutionPlan plan = null;

	/** The id of execution plan. */
	private String executionId = null;

	private WrapperNode wrapperTree = null;

	private ExecutionHandle handle = null;

	private WriterHolder writer;

	/**
	 * The node selector which will be used to select execution nodes that will
	 * be used.
	 */
	private NodePicker nodePicker;

	
	/** Requirements from execution nodes that will be used */
	private String requirements;
	
	/** Environment hint collection */
	private EnvHintCollection Hints = new EnvHintCollection();

	private boolean isLocal = false;
	
	private Object initSync = new Object();
	private boolean isInit = false;
	
	public WorkflowDTSAdaptor(Boolean local) throws Exception {
		this();
		this.isLocal = local;
	}
	
	public WorkflowDTSAdaptor() throws Exception {
		plan = new ExecutionPlan();
		plan.Config = new PlanConfig();
		plan.EnvHints = this.Hints;

		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));

		writer = new WriterHolder();
	}

	public void setTransPlan(TransformationDescription desc) {
		transDesc = desc;
	}

	public void SetScope(String scope) {
		this.scope = scope;
	}

	public void SetExecutionId(String executionId) {
		this.executionId = executionId;
	}
	
	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#CreatePlan()
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException, WorkflowInternalErrorException, WorkflowEnvironmentException {
		if (this.scope == null) {
			throw new WorkflowValidationException("No scope specified");
		}
		if (transDesc == null) {
			throw new WorkflowValidationException("No transformation description specified");
		}
		
		//Set scope to input parameters
		Parameter[] pars = transDesc.getInput().getInputParameters();
		if (pars == null) pars = new Parameter[] {};
		List<Parameter> parsWithScope = new ArrayList<Parameter>(Arrays.asList(pars));
		parsWithScope.add(new Parameter("GCubeActionScope", scope));
		transDesc.getInput().setInputParameters(parsWithScope.toArray(pars));
		
		try {
			nodePicker = new NodePicker(requirements);
		} catch (Exception e1) {
			throw new WorkflowEnvironmentException("Could not find appropriate execution nodes for execution", e1);
		}

		ConstructEnvironmentHints();

		NodeExecutionInfo planInfo = null;

//		// jg
//		try {
//			NodePicker picker = new NodePicker();
//		} catch (Exception e1) {
//			throw new WorkflowInternalErrorException("Node Picker initialization failed", e1);
//		}
//		// jg
		try {
			planInfo = ConstructWorkflow();
			// ((ProcessingWrapper) planInfo.wrapperNode.wrapper).elevate();
			// plan.Variables.Add(((ProcessingWrapper)
			// planInfo.wrapperNode.wrapper).getElevationVariable());
		} catch (Exception e) {
			throw new WorkflowInternalErrorException("Could not construct workflow", e);
		}
		plan.Root = planInfo.element;
		wrapperTree = planInfo.wrapperNode;
	}

	public void addPlan(ArrayList<TransformationUnit> transformationUnits, ArrayList<ContentType> contentTypes) throws Exception {
		synchronized (initSync) {
			while (!isInit)
				initSync.wait();
		}
		
		if (this.scope == null) {
			throw new Exception("No scope specified");
		}
		if (plan.Root == null) {
			throw new Exception("No initial plan created");
		}
		if (handle == null || handle.IsCompletedWithError()) {
			throw new Exception("Merger's Plan has not completed successfully");
		}
		if (transformationUnits.isEmpty()) {
			throw new Exception("Empty Transformation List");
		}
		if (transformationUnits.size() != contentTypes.size()) {
			throw new Exception("Malformed Transformation Plan");
		}

		transDesc.add(transformationUnits, contentTypes);

		WorkflowDTSSubplanAdaptor subAdaptor = null;
		try {
			subAdaptor = new WorkflowDTSSubplanAdaptor(nodePicker, isLocal);
		} catch (Exception e) {
			log.error("Subplan addition initialization failed.");
		}

		subAdaptor.SetEnv(Hints);
		subAdaptor.setTransPlan(transDesc);

		try {
			subAdaptor.CreatePlan();
		}catch(Exception e) {
			try {writer.close();} catch(Exception e1){}
			throw new Exception(e.getMessage());
		}
		
		String locator = null;
		try {
			locator = subAdaptor.ExecutePlan();
			if (locator == null) {
				throw new Exception("Execution failed");
			}
		} catch (Exception e) {
			try {writer.close();} catch(Exception e1){}
			log.error("Subplan addition execution failed.");
			throw e;
		}

		writer.put(locator);
	}

	/*
	 * (non-Javadoc)
	 * 
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
		synchronized (initSync) {
			isInit = true;
			initSync.notify();
		}

		log.info("Executing plan " + executionId);
		ExecutionEngine.Execute(handle);
		synchronized (synchCompletion) {
			while (!obs.IsCompleted()) {
				try {
					synchCompletion.wait();
				} catch (Exception ex) {
					log.error("why am I here?", ex);
				}
			}
		}
		String returnValue = null;
		if (!handle.IsCompleted())
			log.warn("Not completed! Why am I here?");
		else if (handle.IsCompletedWithSuccess()) {
			log.info("Plan successfully completed");
			returnValue = this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue();
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

		log.info("Returning Output Value: " + this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue());

		return returnValue;
	}

	public ExecutionException GetCompletionError() throws WorkflowValidationException {
		if (plan == null)
			throw new WorkflowValidationException("No execution plan has been created");
		return handle.GetCompletionError();
	}

	private NodeExecutionInfo ConstructWorkflow() throws Exception {
		ElementConstructor elementConstructor = new MergeElementConstructor();

		// Wrap output inside a DataTypeConvertable
		DataTypeConvertable outputMerger = new DataTypeConvertable();
		outputMerger.SetConverter(OutputConverter.class.getName());
		outputMerger.SetValue(transDesc.getOutput());

		NamedDataType ndtOutput = new NamedDataType();
		ndtOutput.Value = outputMerger;

		// Set arguments for DataSourceElementConstructor. Set desired source's
		// ContentType
		NamedDataType ndtInputLocator = new NamedDataType();
		ndtInputLocator.Value = new DataTypeResultSet();
		ndtInputLocator.Value.SetValue(writer.getLocator());

		NodeExecutionInfo mergePlanElement = elementConstructor.contructPlanElement(null, new NamedDataType[] { ndtInputLocator, ndtOutput });

		FunctionalityWrapper wrapper = null;
		wrapper = mergePlanElement.wrapperNode.wrapper;
		wrapper.addVariablesToPlan(this.plan);

		IPlanElement bound = ConstructBoundaryElement(mergePlanElement.element);

		// return new NodeExecutionInfo(mergePlanElement.element,
		// mergePlanElement.wrapperNode);
		return new NodeExecutionInfo(bound, mergePlanElement.wrapperNode);
	}

	// private OutputVariableNode ConstructOutputVariableTree(WrapperNode node)
	// {
	// if (node.children == null)
	// return new OutputVariableNode(node.wrapper.getOutputVariable().Name,
	// null);
	// List<OutputVariableNode> children = new ArrayList<OutputVariableNode>();
	// for (WrapperNode wrapperChild : node.children)
	// children.add(ConstructOutputVariableTree(wrapperChild));
	// return new OutputVariableNode(node.wrapper.getOutputVariable().Name,
	// children);
	// }

	@Override
	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IOutputResource> GetOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Construct the Enviroment Hints
	 */
	public void ConstructEnvironmentHints() {
		if (scope != null) {
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));
		}
	}

	private IPlanElement ConstructBoundaryElement(IPlanElement root) throws WorkflowEnvironmentException {
		if (isLocal)
			return root;
		
		BoundaryPlanElement bound = new BoundaryPlanElement();
		bound.CleanUpLocalFiles.clear();
		bound.Triggers.clear();
		bound.Attachments.clear();
		bound.Config = GetBoundaryConfig();
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

	private BoundaryConfig GetBoundaryConfig() throws WorkflowEnvironmentException {
		String node = nodePicker.selectMergerExecutionNode();
		if (node == null || node.contains("null"))
			throw new WorkflowEnvironmentException("Could not find appopriate node for transformation");

		BoundaryConfig Config = new BoundaryConfig();
		Config.HostName = node.split(":")[0];
		Config.Port = Integer.parseInt(node.split(":")[1]);
		Config.NozzleConfig = new TCPServerNozzleConfig(false, 0);
		return Config;
	}

	@Override
	public void finishedAddingPLans() {
		log.info("Finished adding more transformation plans. locator: " + writer.getLocator());
		try {
			writer.close();
		} catch (Exception e) {
			log.warn("Could not close Merger's input");
		}
	}

//	public static void main(String[] args) throws Exception {
//		WorkflowDTSAdaptor adaptor = new WorkflowDTSAdaptor();
//
//		String providerInformationName = "gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";
//		adaptor.SetScope("/gcube/devNext");
//
//		InformationSystem.Init(providerInformationName, adaptor.Hints);
//
//		Input in = new Input("Local", "/home/jgerbe/testArea/src4", null);
//		Output out = new Output("Local", "/home/jgerbe/testArea/dest", null);
//		adaptor.setTransPlan(new TransformationDescription(in, out));
//		adaptor.CreatePlan();
//
//		System.out.println(adaptor.GetCreatedPlan().Serialize());
//	}
}
