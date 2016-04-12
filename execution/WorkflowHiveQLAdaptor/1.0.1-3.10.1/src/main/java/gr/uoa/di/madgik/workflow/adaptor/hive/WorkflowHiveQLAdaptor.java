package gr.uoa.di.madgik.workflow.adaptor.hive;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.commons.infra.HostingNode;
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
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FlowPlanElement;
import gr.uoa.di.madgik.execution.plan.element.PlanElementBase;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionRetry;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.hive.plan.DataSourceNode;
import gr.uoa.di.madgik.hive.plan.Functionality;
import gr.uoa.di.madgik.hive.plan.OperatorNode;
import gr.uoa.di.madgik.hive.plan.PlanNode;
import gr.uoa.di.madgik.hive.test.HiveParserTest;
import gr.uoa.di.madgik.hive.utils.XMLBeautifier;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge.GradualMergeOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2.GRS2Aggregator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2.GRS2Splitter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.MergeOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderHolder;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderInit;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.partition.PartitionOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.select.SelectOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform.ScriptOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.StorageTypes;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.StorageUtils;
import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.hive.nodeselection.NodePicker;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters.MapConverter;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters.StatsConverter;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors.DataSinkElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors.DataSourceElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.elementconstructors.UnaryElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.holders.WriterHolder;
import gr.uoa.di.madgik.workflow.adaptor.hive.utils.wrappers.FunctionalityWrapper;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.JDLParsingUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory.DirectoryEntryType;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workflow adaptor used for creating an Execution plan from a operator plan
 * description.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class WorkflowHiveQLAdaptor implements IWorkflowAdaptor, Runnable {
	/** The logger. */
	private Logger log = LoggerFactory.getLogger(WorkflowHiveQLAdaptor.class.getName());

	/**
	 * The description of the plan from which an execution plan will be created.
	 */
	private PlanNode planDesc = null;

	/** The constructed execution plan. */
	private ExecutionPlan plan = null;

	/** The id of execution plan. */
	private String executionId = null;

	private WrapperNode wrapperTree = null;

	private FunctionalityWrapper partWrapper;
	
	private ExecutionHandle handle = null;
	
	/** Resultset endpoitns will be written here in case of merge operator */
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

	/**
	 * The description of the subplan that will be parallelized.
	 */
	private PlanNode parallelPlanDesc = null;

	private NamedDataType ndtInLoc;
	
	public WorkflowHiveQLAdaptor() throws Exception {
		plan = new ExecutionPlan();
		plan.Config = new PlanConfig();
		plan.EnvHints = this.Hints;

		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));

		writer = new WriterHolder();
	}

	public void setPlanDesc(PlanNode plan) {
		planDesc = plan;
	}

	public void SetExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	
	public void presetInputLocator (String loc) throws ExecutionValidationException, URISyntaxException {
		NamedDataType ndtInLoc = new NamedDataType();
		ndtInLoc.Value = new DataTypeResultSet();
		ndtInLoc.Value.SetValue(new URI(loc));

		this.ndtInLoc = ndtInLoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#CreatePlan()
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException, WorkflowInternalErrorException, WorkflowEnvironmentException {
		if (planDesc == null) {
			throw new WorkflowValidationException("No operator plan description specified");
		}

		try {
//			nodePicker = new NodePicker(requirements); // XXX uncomment
		} catch (Exception e1) {
			throw new WorkflowEnvironmentException("Could not find appropriate execution nodes for execution", e1);
		}

		NodeExecutionInfo planInfo = null;

		try {
			planInfo = ConstructWorkflow(planDesc);
		} catch (Exception e) {
			throw new WorkflowInternalErrorException("Could not construct workflow", e);
		}
		plan.Root = planInfo.element;
		wrapperTree = planInfo.wrapperNode;
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

	public String ExecutePlan() throws WorkflowValidationException, ExecutionException {
		if (plan.Root == null)
			throw new WorkflowValidationException("No execution plan has been created");

		handle = ExecutionEngine.Submit(plan);
		Object synchCompletion = new Object();
		executionId = ExecutionDirectory.ReserveKey();
		ExecutionObserver obs = new ExecutionObserver(executionId, DirectoryEntryType.Generic, -1l, handle, this, false, synchCompletion);
		ExecutionDirectory.Register(obs);
		handle.RegisterObserver(obs);

		log.info("Executing plan " + executionId);
		ExecutionEngine.Execute(handle);
		
		// Check if plan is parallelizable and start thread
		if (partWrapper != null)
			new Thread(this).start();
		
		synchronized (synchCompletion) {
			while (!obs.IsCompleted()) {
				try {
					synchCompletion.wait();
				} catch (Exception ex) {
				}
			}
		}
		if (!handle.IsCompleted())
			log.warn("Not completed! Why am I here?");
		else if (handle.IsCompletedWithSuccess())
			log.info("Plan successfully completed");
		else if (handle.IsCompletedWithError()) {
			String errorString = "Plan unsuccessfully completed with error";
			if (handle.GetCompletionError() instanceof ExecutionRunTimeException)
				errorString += " of cause " + ((ExecutionRunTimeException) this.handle.GetCompletionError()).GetCauseFullName();
			log.info(errorString, handle.GetCompletionError());
		} else
			log.warn("Completed but neither with success or failure!");

		log.info("Returning Output Value: " + this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue());

		return this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue();
	}
	
	@Override
	public void run() {
		int cntLocators = 0;

		try {
			String locator;
			int i = 0;
			while(true) {
				locator = partWrapper.getOutputVariable().Value.GetStringValue();
				log.info("haha: " + locator);
				if (locator.isEmpty())
					Thread.sleep(i*10 > 1000? 1000 : ++i*10); // XXX nothing better than polling? reduce time
				else
					break;
			}
			ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(new URI(locator));
			while (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))) {
				GenericRecord rec = reader.get(60, TimeUnit.SECONDS);
				// In case a timeout occurs while optimistically waiting for
				// more records form an originally open writer
				if (rec == null) {
					if (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))) {
						log.debug("No new resultset received after 60 secs");
						continue;
					} else {
						log.info("Input Locator closed");
						break;
					}
				}
				// Retrieve the required field of the type available in the gRS
				// definitions
				String loc = ((StringField) rec.getField(0)).getPayload();
				log.info("Got (" + ++cntLocators + ") locator: " + loc + " that will be added for merge");

				WorkflowHiveQLAdaptor adaptor = new WorkflowHiveQLAdaptor();
				adaptor.setPlanDesc(parallelPlanDesc);
				adaptor.presetInputLocator(loc);
				
				adaptor.CreatePlan();
//				log.info("Execution subPlan:\n" + XMLBeautifier.prettyPrintXml(adaptor.GetCreatedPlan().Serialize()));

				String output = adaptor.ExecutePlan();

				writer.put(output);
				
				log.info("Execution id: " + adaptor.GetExecutionID());
			}
			// Close the reader to release and dispose any resources in both
			// reader and writer sides
			try {
				reader.close();
				writer.close();
			} catch (Exception e) {
				log.warn("could not close reader or writer");
			}
		} catch (Exception ex) {
			log.error("Caught unxepected exception", ex);
		}
	}

	public ExecutionException GetCompletionError() throws WorkflowValidationException {
		if (plan == null)
			throw new WorkflowValidationException("No execution plan has been created");
		return handle.GetCompletionError();
	}

	private NodeExecutionInfo ConstructWorkflow(PlanNode planDesc) throws Exception {
		SequencePlanElement seq = null;
		NamedDataType ndtInputToNext = ndtInLoc;

		if(seq == null)
			seq = new SequencePlanElement();
		
		NodeExecutionInfo childExecutionInfo = null;
		if (planDesc instanceof OperatorNode) {
			// Extract parallel subplan
			if (((OperatorNode) planDesc).getFunctionality().equals(Functionality.MERGE)) {
				if (parallelPlanDesc == null) {
					if (((OperatorNode) planDesc).getChildren().size() > 1)
						throw new Exception("Only one root node expected");
					parallelPlanDesc = new OperatorNode((OperatorNode) ((OperatorNode) planDesc).getChildren().get(0));
				} else
					throw new Exception("Paraller plan has already been specified");

				OperatorNode op = (OperatorNode) parallelPlanDesc;
				while (!((OperatorNode) op.getChildren().get(0)).getFunctionality().equals(Functionality.PARTITION))
					op = (OperatorNode) op.getChildren().get(0);
				op.setChildren(new ArrayList<PlanNode>());

				OperatorNode origPlan = (OperatorNode) planDesc;
				while (!((OperatorNode) origPlan.getChildren().get(0)).getFunctionality().equals(Functionality.PARTITION)) {
					origPlan.setChildren(((OperatorNode) origPlan.getChildren().get(0)).getChildren());
				}
			}

			if (!((OperatorNode)planDesc).getChildren().isEmpty()) {
				childExecutionInfo = ConstructWorkflow(((OperatorNode)planDesc).getChildren().get(0));
				ndtInputToNext = childExecutionInfo.wrapperNode.wrapper.getOutputVariable();
				seq = ((SequencePlanElement)childExecutionInfo.element);
			}
		}
		
		WrapperNode wrapperNode = null;
		for (NodeExecutionInfo nodeExecutionInfo : createElement(planDesc, ndtInputToNext)) {
			seq.ElementCollection.add(nodeExecutionInfo.element);

			wrapperNode = nodeExecutionInfo.wrapperNode;
			wrapperNode.wrapper.addVariablesToPlan(this.plan);
		}

		return new NodeExecutionInfo(seq, wrapperNode);
		// BoundaryPlanElement bound = ConstructBoundaryElement();
		// bound.Root = nodeExecutionInfo.element;
		// return new NodeExecutionInfo(bound, nodeExecutionInfo.wrapperNode);
	}
	
	private NodeExecutionInfo[] createElement(PlanNode node, NamedDataType ndtInputLocator) throws Exception {
		ArrayList<NodeExecutionInfo> nodeExecutionInfos = new ArrayList<NodeExecutionInfo>();

		if (node instanceof DataSourceNode) {
			DataSourceNode source = (DataSourceNode) node;
			DataSourceElementConstructor dataSourceElementConstructor = new DataSourceElementConstructor();

			// Wrap input
			DataTypeString inputType = new DataTypeString();
			inputType.SetStringValue(StorageUtils.evaluate(source.getFunctionalArgs().get("source")).name());
			NamedDataType ndtInputType = new NamedDataType();
			ndtInputType.Value = inputType;
			
			DataTypeString inputValue = new DataTypeString();
			inputValue.SetStringValue(StorageUtils.removeProtocolPrefix(source.getFunctionalArgs().get("source")));
			NamedDataType ndtInputValue = new NamedDataType();
			ndtInputValue.Value = inputValue;
			
			Map<String, String> args = new HashMap<String, String>();
			args.put("source", source.getFunctionalArgs().get("source"));
			args.put("delimiter", source.getFunctionalArgs().get("delimiter"));
			args.put("filterMask", source.getFunctionalArgs().get("filterMask"));
			DataTypeConvertable inputParameters = new DataTypeConvertable();
			inputParameters.SetConverter(MapConverter.class.getName());
			inputParameters.SetValue((Map<String, String>)args);

			NamedDataType ndtInputPars = new NamedDataType();
			ndtInputPars.Value = inputParameters;

			NodeExecutionInfo splitterPlanElement = dataSourceElementConstructor
					.contructPlanElement(null, new NamedDataType[] { ndtInputType, ndtInputValue, ndtInputPars });

			nodeExecutionInfos.add(splitterPlanElement);
					
			ndtInputLocator = splitterPlanElement.wrapperNode.wrapper.getOutputVariable();
			
			if (StorageUtils.evaluate(node.getFunctionalArgs().get("source")).equals(StorageTypes.FTP)
					|| StorageUtils.evaluate(node.getFunctionalArgs().get("source")).equals(StorageTypes.PATH)) {
				UnaryElementConstructor splitterElementConstructor = new UnaryElementConstructor();

				DataTypeString operatorType = new DataTypeString();
				operatorType.SetStringValue(GRS2Splitter.class.getName());
				NamedDataType ndtOperatorType = new NamedDataType();
				ndtOperatorType.Value = operatorType;

				DataTypeConvertable outputParams = new DataTypeConvertable();
				outputParams.SetConverter(MapConverter.class.getName());
				outputParams.SetValue((Map<String, String>) node.getFunctionalArgs());
				NamedDataType ndtOutputParams = new NamedDataType();
				ndtOutputParams.Value = outputParams;

				DataTypeConvertable statsContainer = new DataTypeConvertable();
				statsContainer.SetConverter(StatsConverter.class.getName());
				statsContainer.SetValue(new StatsContainer());
				NamedDataType ndtStatsContainer = new NamedDataType();
				ndtStatsContainer.Value = statsContainer;

				nodeExecutionInfos.add(splitterElementConstructor.contructPlanElement(null, new NamedDataType[] { ndtOperatorType,
						ndtInputLocator, ndtOutputParams, ndtStatsContainer }));
			}
			
			return nodeExecutionInfos.toArray(new NodeExecutionInfo[nodeExecutionInfos.size()]);
		}

		OperatorNode operator = (OperatorNode) node;
		switch (operator.getFunctionality()) {
		case SELECT:
		{
			UnaryElementConstructor selectElementConstructor = new UnaryElementConstructor();

			DataTypeString operatorClassName = new DataTypeString();
			operatorClassName.SetStringValue(SelectOp.class.getName());
			NamedDataType ndtOperatorClassName = new NamedDataType();
			ndtOperatorClassName.Value = operatorClassName;

			DataTypeConvertable operatorParams = new DataTypeConvertable();
			operatorParams.SetConverter(MapConverter.class.getName());
			operatorParams.SetValue((Map<String, String>)operator.getFunctionalArgs());
			NamedDataType ndtOperatorParams = new NamedDataType();
			ndtOperatorParams.Value = operatorParams;
			
			DataTypeConvertable statsContainer = new DataTypeConvertable();
			statsContainer.SetConverter(StatsConverter.class.getName());
			statsContainer.SetValue(new StatsContainer());
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.Value = statsContainer;

			nodeExecutionInfos.add(selectElementConstructor
					.contructPlanElement(SelectOp.class, new NamedDataType[] { ndtOperatorClassName, ndtInputLocator, ndtOperatorParams, ndtStatsContainer }));
			break;
		}
		case SCRIPT:
		{
			UnaryElementConstructor scriptElementConstructor = new UnaryElementConstructor();

			DataTypeString operatorClassName = new DataTypeString();
			operatorClassName.SetStringValue(ScriptOp.class.getName());
			NamedDataType ndtOperatorClassName = new NamedDataType();
			ndtOperatorClassName.Value = operatorClassName;

			DataTypeConvertable operatorParams = new DataTypeConvertable();
			operatorParams.SetConverter(MapConverter.class.getName());
			operatorParams.SetValue((Map<String, String>)operator.getFunctionalArgs());
			NamedDataType ndtOperatorParams = new NamedDataType();
			ndtOperatorParams.Value = operatorParams;
			
			DataTypeConvertable statsContainer = new DataTypeConvertable();
			statsContainer.SetConverter(StatsConverter.class.getName());
			statsContainer.SetValue(new StatsContainer());
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.Value = statsContainer;

			nodeExecutionInfos.add(scriptElementConstructor
					.contructPlanElement(SelectOp.class, new NamedDataType[] { ndtOperatorClassName, ndtInputLocator, ndtOperatorParams, ndtStatsContainer }));
			break;
		}
		case PARTITION:
		{
			UnaryElementConstructor partitionElementConstructor = new UnaryElementConstructor();

			DataTypeString operatorClassName = new DataTypeString();
			operatorClassName.SetStringValue(PartitionOp.class.getName());
			NamedDataType ndtOperatorClassName = new NamedDataType();
			ndtOperatorClassName.Value = operatorClassName;

			DataTypeConvertable operatorParams = new DataTypeConvertable();
			operatorParams.SetConverter(MapConverter.class.getName());
			operatorParams.SetValue((Map<String, String>)operator.getFunctionalArgs());
			NamedDataType ndtOperatorParams = new NamedDataType();
			ndtOperatorParams.Value = operatorParams;
			
			DataTypeConvertable statsContainer = new DataTypeConvertable();
			statsContainer.SetConverter(StatsConverter.class.getName());
			statsContainer.SetValue(new StatsContainer());
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.Value = statsContainer;

			NodeExecutionInfo partitionPlanElement = partitionElementConstructor
			.contructPlanElement(SelectOp.class, new NamedDataType[] { ndtOperatorClassName, ndtInputLocator, ndtOperatorParams, ndtStatsContainer });
			nodeExecutionInfos.add(partitionPlanElement);
			
			if (partWrapper != null)
				throw new Exception("Single partition is supported");
			partWrapper = partitionPlanElement.wrapperNode.wrapper;
			break;
		}
		case MERGE:
		{
			UnaryElementConstructor mergeElementConstructor = new UnaryElementConstructor();

			DataTypeString operatorClassName = new DataTypeString();
			operatorClassName.SetStringValue(GradualMergeOp.class.getName());
			NamedDataType ndtOperatorClassName = new NamedDataType();
			ndtOperatorClassName.Value = operatorClassName;

			writer = new WriterHolder();
			NamedDataType ndtInLoc = new NamedDataType();
			ndtInLoc.Value = new DataTypeResultSet();
			ndtInLoc.Value.SetValue(writer.getLocator());
			
			DataTypeConvertable operatorParams = new DataTypeConvertable();
			operatorParams.SetConverter(MapConverter.class.getName());
			operatorParams.SetValue((Map<String, String>)operator.getFunctionalArgs());
			NamedDataType ndtOperatorParams = new NamedDataType();
			ndtOperatorParams.Value = operatorParams;
			
			DataTypeConvertable statsContainer = new DataTypeConvertable();
			statsContainer.SetConverter(StatsConverter.class.getName());
			statsContainer.SetValue(new StatsContainer());
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.Value = statsContainer;

			nodeExecutionInfos.add(mergeElementConstructor
					.contructPlanElement(SelectOp.class, new NamedDataType[] { ndtOperatorClassName, ndtInLoc, ndtOperatorParams, ndtStatsContainer }));

			break;
		}
		case DATASINK:
		{
			// if datasink is file based gather result and sent the to datasink afterwards
			if (StorageUtils.evaluate(node.getFunctionalArgs().get("sink")).equals(StorageTypes.FTP) ||
				StorageUtils.evaluate(node.getFunctionalArgs().get("sink")).equals(StorageTypes.PATH)) {
				UnaryElementConstructor agrregatorElementConstructor = new UnaryElementConstructor();

				DataTypeString operatorType = new DataTypeString();
				operatorType.SetStringValue(GRS2Aggregator.class.getName());
				NamedDataType ndtOperatorType = new NamedDataType();
				ndtOperatorType.Value = operatorType;
				
				DataTypeConvertable outputParams = new DataTypeConvertable();
				outputParams.SetConverter(MapConverter.class.getName());
				outputParams.SetValue((Map<String, String>) node.getFunctionalArgs());
				NamedDataType ndtOutputParams = new NamedDataType();
				ndtOutputParams.Value = outputParams;

				DataTypeConvertable statsContainer = new DataTypeConvertable();
				statsContainer.SetConverter(StatsConverter.class.getName());
				statsContainer.SetValue(new StatsContainer());
				NamedDataType ndtStatsContainer = new NamedDataType();
				ndtStatsContainer.Value = statsContainer;

				NodeExecutionInfo sinkPlanElement = agrregatorElementConstructor
						.contructPlanElement(null, new NamedDataType[] { ndtOperatorType, ndtInputLocator, ndtOutputParams, ndtStatsContainer });
				
				nodeExecutionInfos.add(sinkPlanElement);
				
				ndtInputLocator = sinkPlanElement.wrapperNode.wrapper.getOutputVariable();
			}
			
			DataSinkElementConstructor sinkElementConstructor = new DataSinkElementConstructor();

			DataTypeString outputType = new DataTypeString();
			outputType.SetStringValue(StorageUtils.evaluate(node.getFunctionalArgs().get("sink")).name());
			NamedDataType ndtOutputType = new NamedDataType();
			ndtOutputType.Value = outputType;
			
			DataTypeString outputValue = new DataTypeString();
			outputValue.SetStringValue(StorageUtils.removeProtocolPrefix(node.getFunctionalArgs().get("sink")));
			NamedDataType ndtOutputValue = new NamedDataType();
			ndtOutputValue.Value = outputValue;
			
			DataTypeConvertable outputParams = new DataTypeConvertable();
			outputParams.SetConverter(MapConverter.class.getName());
			outputParams.SetValue((Map<String, String>) node.getFunctionalArgs());
			NamedDataType ndtOutputParams = new NamedDataType();
			ndtOutputParams.Value = outputParams;

			DataTypeConvertable statsContainer = new DataTypeConvertable();
			statsContainer.SetConverter(StatsConverter.class.getName());
			statsContainer.SetValue(new StatsContainer());
			NamedDataType ndtStatsContainer = new NamedDataType();
			ndtStatsContainer.Value = statsContainer;

			nodeExecutionInfos.add(sinkElementConstructor
					.contructPlanElement(null, new NamedDataType[] { ndtInputLocator, ndtOutputType, ndtOutputValue, ndtOutputParams, ndtStatsContainer }));
			break;
		}
		default:
			throw new Exception("Unsupported functionality");
		}

		return nodeExecutionInfos.toArray(new NodeExecutionInfo[nodeExecutionInfos.size()]);
	}
	
	/**
	 * Get the result set that is passed to merger
	 * @return the WriterHolder
	 */
	public WriterHolder getWriter() {
		return writer;
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
	public void ConstructEnvironmentHints(String scope) {
		if (scope != null) {
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));
		}
	}

	private BoundaryPlanElement ConstructBoundaryElement() throws WorkflowEnvironmentException {
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
		return bound;
	}

	private BoundaryConfig GetBoundaryConfig() throws WorkflowEnvironmentException {
		String node = "dl14.di.uoa.gr:4000";
		
		BoundaryConfig Config = new BoundaryConfig();
		Config.HostName = node.split(":")[0];
		Config.Port = Integer.parseInt(node.split(":")[1]);
		Config.NozzleConfig = new TCPServerNozzleConfig(false, 0);
		return Config;
	}
	
	
//	/**
//	 * Creates the input sandbox retrieve element to retrieve from storage system the files that are defined in the 
//	 * input sandbox of a job
//	 * 
//	 * @param attachment the attachment that describes the resource
//	 * @param internal the parsed info
//	 * 
//	 * @return the file transfer plan element
//	 * 
//	 * @throws WorkflowValidationException A validation error occurred
//	 */
//	private FileTransferPlanElement CreateInputSandboxRetrieveElement(AttachedJDLResource attachment,ParsedJDLInfo internal) throws WorkflowValidationException
//	{
//		FileTransferPlanElement ftr=new FileTransferPlanElement();
//		if(JDLParsingUtils.IsSandboxNameReference(attachment.Key))
//		{
//			throw new WorkflowValidationException("Provided sandbox name is of root reference");
//		}
//		ftr.Direction=TransferDirection.Retrieve;
//		ftr.Input=new SimpleInParameter();
//		if(attachment.Key.equals(internal.Executable)) ftr.IsExecutable=true;
//		NamedDataType ndtAttachment=new NamedDataType();
//		ndtAttachment.IsAvailable=true;
//		ndtAttachment.Name=UUID.randomUUID().toString();
//		ndtAttachment.Token=ndtAttachment.Name;
//		ndtAttachment.Value=new DataTypeString();
//		this.Plan.Variables.Add(ndtAttachment);
//		try
//		{
//			((DataTypeString)ndtAttachment.Value).SetValue(attachment.StorageSystemID);
//		}catch(Exception ex)
//		{
//			throw new WorkflowValidationException("Could not create execution plan",ex);
//		}
//		((SimpleInParameter)ftr.Input).VariableName=ndtAttachment.Name;
//		ftr.Output=new SimpleOutParameter();
//		NamedDataType ndtRetrievedAttachment=new NamedDataType();
//		ndtRetrievedAttachment.IsAvailable=false;
//		ndtRetrievedAttachment.Name=UUID.randomUUID().toString();
//		ndtRetrievedAttachment.Token=ndtRetrievedAttachment.Name;
//		ndtRetrievedAttachment.Value=new DataTypeString();
//		this.Plan.Variables.Add(ndtRetrievedAttachment);
//		((SimpleOutParameter)ftr.Output).VariableName=ndtRetrievedAttachment.Name;
//		ftr.MoveTo=new SimpleInParameter();
//		NamedDataType ndtRename=new NamedDataType();
//		ndtRename.IsAvailable=true;
//		ndtRename.Name=UUID.randomUUID().toString();
//		ndtRename.Token=ndtRename.Name;
//		ndtRename.Value=new DataTypeString();
//		String RenameTo=attachment.Key;
//		if(RenameTo==null || RenameTo.trim().length()==0) throw new WorkflowValidationException("Defined resource name is not valid");
//		try
//		{
//			((DataTypeString)ndtRename.Value).SetValue(RenameTo);
//		}catch(Exception ex)
//		{
//			throw new WorkflowValidationException("Could not create execution plan",ex);
//		}
//		this.Plan.Variables.Add(ndtRename);
//		((SimpleInParameter)ftr.MoveTo).VariableName=ndtRename.Name;
//		return ftr;
//	}
	
//	private BoundaryConfig GetBoundaryConfig() throws WorkflowEnvironmentException {
//		String node = nodePicker.selectMergerExecutionNode();
//		if (node == null || node.contains("null"))
//			throw new WorkflowEnvironmentException("Could not find appopriate node for transformation");
//
//		BoundaryConfig Config = new BoundaryConfig();
//		Config.HostName = node.split(":")[0];
//		Config.Port = Integer.parseInt(node.split(":")[1]);
//		Config.NozzleConfig = new TCPServerNozzleConfig(false, 0);
//		return Config;
//	}
	
//	/**
//	 * Construct the {@link BoundaryPlanElement} that describes the execution of a single job. The resources of 
//	 * the Input Sandbox are retrieved, the Boundary Config if provided is used otherwise a new node is picked 
//	 * and finally the {@link ShellPlanElement} that describes the execution is constructed
//	 * 
//	 * @param internal the parsed plan
//	 * @param bConfig the boundary config
//	 * @param NodeName the node name
//	 * 
//	 * @return the boundary plan element constructed
//	 * 
//	 * @throws WorkflowEnvironmentException An error of the environment occurred
//	 * @throws WorkflowValidationException A validation error occurred
//	 */
//	private PlanElementBase ConstructJobFlow(String executable) throws WorkflowEnvironmentException, WorkflowValidationException
//	{
//		SequencePlanElement seqRemote=new SequencePlanElement();
//		FlowPlanElement flowRemote = new FlowPlanElement();
//
//		seqRemote.ElementCollection.add(this.CreateInputSandboxRetrieveElement(att, internal));
//
//		TryCatchFinallyPlanElement tcf=new TryCatchFinallyPlanElement();
//		flowRemote.ElementCollection.add(tcf);
//		seqRemote.ElementCollection.add(flowRemote);
//		tcf.TryFlow=this.CreateExecutableElement(internal);
//		tcf.CatchFlows.clear();
//		tcf.FinallyFlow=new SequencePlanElement();
//		
//		
//		if(((SequencePlanElement)tcf.FinallyFlow).ElementCollection.size()==0) tcf.FinallyFlow=null;
//		return seqRemote;
//	}
//	
//	
//	/**
//	 * Creates the executable element wrapped in a {@link ShellPlanElement}. This is the executable unit wrapping the
//	 * defined executable of a job. The arguments defined for the job are split to multiple arguments using the space 
//	 * character as a delimiter. If a retry count has been defined it is also used at this level.
//	 * 
//	 * @param internal the parsed info
//	 * 
//	 * @return the shell plan element
//	 * 
//	 * @throws WorkflowValidationException A validation error occurred
//	 */
//	private ShellPlanElement CreateExecutableElement(ParsedJDLInfo internal) throws WorkflowValidationException
//	{
//		ShellPlanElement shell=new ShellPlanElement();
//		logger.info("Shell plan element arguments: " + internal.Arguments);
//		if(internal.Arguments!=null)
//		{
//			String [] args=internal.Arguments.trim().split("\\s");
//			for(String arg : args)
//			{
//				SimpleInParameter argParameter=new SimpleInParameter();
//				NamedDataType ndtArgParameter=new NamedDataType();
//				ndtArgParameter.IsAvailable=true;
//				ndtArgParameter.Name=UUID.randomUUID().toString();
//				ndtArgParameter.Token=ndtArgParameter.Name;
//				ndtArgParameter.Value=new DataTypeString();
//				try
//				{
//					((DataTypeString)ndtArgParameter.Value).SetValue(arg);
//				}catch(Exception ex)
//				{
//					throw new WorkflowValidationException("Could not create execution plan",ex);
//				}
//				argParameter.VariableName=ndtArgParameter.Name;
//				this.Plan.Variables.Add(ndtArgParameter);
//				shell.ArgumentParameters.add(new AttributedInputParameter(argParameter));
//			}
//		}
//		shell.Command=internal.Executable;
//		if(internal.Error!=null)
//		{
//			SimpleInOutParameter stdErrParameter=new SimpleInOutParameter();
//			NamedDataType ndtStdErrParameter=new NamedDataType();
//			ndtStdErrParameter.IsAvailable=true;
//			ndtStdErrParameter.Name=UUID.randomUUID().toString();
//			ndtStdErrParameter.Token=ndtStdErrParameter.Name;
//			ndtStdErrParameter.Value=new DataTypeString();
//			try
//			{
//				((DataTypeString)ndtStdErrParameter.Value).SetValue(internal.Error);
//			}catch(Exception ex)
//			{
//				throw new WorkflowValidationException("Could not create execution plan",ex);
//			}
//			stdErrParameter.VariableName=ndtStdErrParameter.Name;
//			this.Plan.Variables.Add(ndtStdErrParameter);
//			shell.StdErrParameter=stdErrParameter;
//			shell.StdErrIsFile=true;
//		}
//		SimpleOutParameter exitValueParameter=new SimpleOutParameter();
//		NamedDataType ndtExitValueParameter=new NamedDataType();
//		ndtExitValueParameter.IsAvailable=false;
//		ndtExitValueParameter.Name=UUID.randomUUID().toString();
//		ndtExitValueParameter.Token=ndtExitValueParameter.Name;
//		ndtExitValueParameter.Value=new DataTypeString();
//		exitValueParameter.VariableName=ndtExitValueParameter.Name;
//		this.Plan.Variables.Add(ndtExitValueParameter);
//		shell.StdExitValueParameter=exitValueParameter;
//		if(internal.Input!=null)
//		{
//			SimpleInParameter stdInParameter=new SimpleInParameter();
//			NamedDataType ndtStdInParameter=new NamedDataType();
//			ndtStdInParameter.IsAvailable=true;
//			ndtStdInParameter.Name=UUID.randomUUID().toString();
//			ndtStdInParameter.Token=ndtStdInParameter.Name;
//			ndtStdInParameter.Value=new DataTypeString();
//			try
//			{
//				((DataTypeString)ndtStdInParameter.Value).SetValue(internal.Input);
//			}catch(Exception ex)
//			{
//				throw new WorkflowValidationException("Could not create execution plan",ex);
//			}
//			stdInParameter.VariableName=ndtStdInParameter.Name;
//			this.Plan.Variables.Add(ndtStdInParameter);
//			shell.StdInParameter=stdInParameter;
//			shell.StdInIsFile=true;
//		}
//		if(internal.Output!=null)
//		{
//			SimpleInOutParameter stdOutParameter=new SimpleInOutParameter();
//			NamedDataType ndtStdOutParameter=new NamedDataType();
//			ndtStdOutParameter.IsAvailable=true;
//			ndtStdOutParameter.Name=UUID.randomUUID().toString();
//			ndtStdOutParameter.Token=ndtStdOutParameter.Name;
//			ndtStdOutParameter.Value=new DataTypeString();
//			try
//			{
//				((DataTypeString)ndtStdOutParameter.Value).SetValue(internal.Output);
//			}catch(Exception ex)
//			{
//				throw new WorkflowValidationException("Could not create execution plan",ex);
//			}
//			stdOutParameter.VariableName=ndtStdOutParameter.Name;
//			this.Plan.Variables.Add(ndtStdOutParameter);
//			shell.StdOutParameter=stdOutParameter;
//			shell.StdOutIsFile=true;
//		}
//		shell.ExitCodeErrors.clear();
//		shell.Triggers.clear();
//		if(internal.RetryCount>0)
//		{
//			ContingencyTrigger trigg=new ContingencyTrigger();
//			trigg.IsFullNameOfError=false;
//			trigg.TriggeringError=null;
//			trigg.Reaction=new ContingencyReactionRetry();
//			((ContingencyReactionRetry)trigg.Reaction).NumberOfRetries=internal.RetryCount;
//			((ContingencyReactionRetry)trigg.Reaction).RetryInterval=(internal.RetryInterval>0 ? internal.RetryInterval : ParsedJDLInfo.DefaultRetryInterval);
//			shell.Triggers.add(trigg);
//		}
//		shell.Environment.clear();
//		for(EnvironmentKeyValue envkv : internal.Environment) shell.Environment.add(envkv);
//		return shell;
//	}
}
