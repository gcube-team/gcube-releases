package gr.uoa.di.madgik.workflow.adaptor;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.MinimumCollocationPolicy;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.NodeAssignmentPolicy;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.random.RandomNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.ru.LRUNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.ru.MRUNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.ru.RUNodeSelector;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.environment.infra.NodeInfo2HostingNodeAdapter;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig.ConnectionMode;
import gr.uoa.di.madgik.execution.plan.element.BagPlanElement;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.FlowPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.WSSOAPPlanElement;
import gr.uoa.di.madgik.execution.plan.element.condition.BagConditionalElement;
import gr.uoa.di.madgik.execution.plan.element.condition.BagElementDependencyPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeLeaf;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionRetry;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXPathFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterXPathFilter.OutputResultType;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPCall;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.execution.plan.trycatchfinally.CatchElement;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.IParsedInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.IWorkflowParser;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AdaptorJDLResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.JDLParser;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.JDLParsingUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.OutputSandboxJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobDescriptionType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.ParsedJDLInfo.JobType;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.utils.SOAPBuilder;
import gr.uoa.di.madgik.workflow.wrappers.ServiceWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.lang.model.element.Element;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The Class WorkflowJDLAdaptor constructs an {@link ExecutionPlan} based on the
 * description of a job defined in a description using the JDL syntax. This
 * description can be of a single job or it can include a DAG of jobs. The JDL
 * description is parsed using the {@link JDLParser} and the adaptor then
 * processed the retrieved {@link ParsedJDLInfo} to create the
 * {@link ExecutionPlan}
 * </p>
 * <p>
 * The {@link AdaptorJDLResources} provided include all the
 * {@link AttachedJDLResource} items that are expected as input from the jobs
 * defined. The output resources of the workflow, retrievable as
 * {@link OutputSandboxJDLResource} instances, are constructed from the elements
 * found in all the jobs Output Sandbox.
 * </p>
 * <p>
 * Depending on the configuration, the adaptor will create the
 * {@link ExecutionPlan} that will orchestrate the execution of a DAG of jobs
 * either as a series of {@link SequencePlanElement} and {@link FlowPlanElement}
 * elements or as a single {@link BagPlanElement}. The first case allows for a
 * well defined series of operation but since the creation of such a series of
 * constructs is an exercise on graph topological sorting, which as a problem
 * can provide multiple answers that depending on the nature of the original
 * graph might restrict the parallelization factor of the overall DAG, in cases
 * of complex graphs, this case can damage the parallelization capabilities of n
 * execution plan. The second case is much more dynamic. It allows for execution
 * time decision making of the nodes to be executed. This of course comes as a
 * tradeoff with increased complexity at runtime with respect to the well
 * defined plan, but it can provide the optimal parallelization capabilities.
 * </p>
 * <p>
 * Staging of input files for the executables is performed at a level of Input
 * Sandbox defined for each job. The resources that are attached to the adaptor
 * are stored in the Storage System and are retrieved in the node that hosts
 * that defines the input sandbox that needs them. The files declared in the
 * Output Sandbox of a job are stored in the Storage System and information on
 * the way to retrieve the output is provided through the
 * {@link OutputSandboxJDLResource} which is valid after the completion of the
 * execution.
 * </p>
 * 
 * TODO: - Supported jobs are only those of type Normal - The case of node
 * collocation is not handled correctly because multiple
 * {@link BoundaryPlanElement} are created. The node used is still a single one
 * but it is contacted multiple times and data locality is not exploited
 * correctly. - The arguments defined for an executable in the respective JDL
 * attribute, when passed to the {@link ShellPlanElement} are split using the
 * space character (' ') as a delimiter. This way no space containing phrase can
 * be passed a single argument - The Retry and Shallow Retry attributes of the
 * JDl are treated equally and are used at the level of {@link ShellPlanElement}
 * and not at the level of {@link BoundaryPlanElement} - After the execution
 * completion not cleanup in the Storage system is done.
 * 
 * @author gpapanikos
 */
public class WorkflowJDLAdaptor implements IWorkflowAdaptor {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkflowJDLAdaptor.class);

	/** The Parser. */
	private IWorkflowParser Parser = null;

	/** The Resources needed to construct and execute the plan. */
	private AdaptorJDLResources Resources = null;

	/** The constructed execution Plan. */
	private ExecutionPlan Plan = null;

	/**
	 * The execution id associated with the created Plan. Can be set optionally.
	 */
	private String ExecutionId = null;

	/** The Output resources that will contain info on the produced output. */
	private Set<IOutputResource> OutputResources = null;

	/** The JDL file. */
	private File JDLFile = null;

	/** The JDL description. */
	private String JDLDescription = null;

	private EnvHintCollection Hints = new EnvHintCollection();

	private HashMap<String, HashSet<FileTransferPlanElement>> Staging = new HashMap<String, HashSet<FileTransferPlanElement>>();

	private NodeSelector NodeSelector = new MRUNodeSelector();

	/** maximum concurrent transfers per node */
	private final int MAX_TRANSFERS = 5;

	private Map<String,String> WSVariables = new HashMap<String, String>();
	/**
	 * Instantiates a new workflow jdl adaptor.
	 */
	public WorkflowJDLAdaptor() {
		this.Parser = new JDLParser();
		this.OutputResources = new HashSet<IOutputResource>();
	}

	/**
	 * Sets the resources that the adaptor needs to construct the plan. These
	 * must be of type {@link AdaptorJDLResources}.
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#SetAdaptorResources(gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources)
	 */
	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException {
		if (!(Resources instanceof AdaptorJDLResources))
			throw new WorkflowValidationException("Invalid adaptor resources provided");
		this.Resources = (AdaptorJDLResources) Resources;
	}

	/**
	 * Sets the jdl file
	 * 
	 * @param jdlFile
	 *            the jdl file
	 */
	public void SetJDL(File jdlFile) {
		this.JDLFile = jdlFile;
	}

	/**
	 * Sets the jdl description
	 * 
	 * @param jdlDescription
	 *            the jdl description
	 */
	public void SetJDL(String jdlDescription) {
		this.JDLDescription = jdlDescription;
	}

	public void SetExecutionId(String executionId) {
		this.ExecutionId = executionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#CreatePlan()
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException, WorkflowInternalErrorException, WorkflowEnvironmentException {
		if (this.Resources == null)
			throw new WorkflowValidationException("No resources specified");
		if (this.JDLFile != null)
			this.Parser.Parse(this.JDLFile);
		else if (this.JDLDescription != null)
			this.Parser.Parse(this.JDLDescription);
		else
			throw new WorkflowValidationException("No jdl specified");
		this.ConstructWorkflow();
		this.ExcludeOutputResourcesCleanUp();
	}

	private void ExcludeOutputResourcesCleanUp() {
		for (IOutputResource res : this.OutputResources) {
			if (!(res instanceof OutputSandboxJDLResource))
				continue;
			this.Plan.CleanUpSSExclude.Add(((OutputSandboxJDLResource) res).VariableID);
		}
	}

	public void ConstructEnvironmentHints(String Scope) {
		if (Scope != null) {
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(Scope)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetCreatedPlan()
	 */
	public ExecutionPlan GetCreatedPlan() {
		return this.Plan;
	}

	/**
	 * Retrieves the set of output resources containing info on the products of
	 * the workflow after the plan is executed. the resources are of type
	 * {@link OutputSandboxJDLResource}. These resources are determined by the
	 * Output Sandbox found in the provided JDL.
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetOutput()
	 */
	public Set<IOutputResource> GetOutput() {
		return this.OutputResources;
	}

	/**
	 * Validate the parsed info. In order to be valid, All jobs defined in the
	 * JDL must be of type {@link ParsedJDLInfo.JobType#Normal} or
	 * {@link ParsedJDLInfo.JobType#WS}. In case the JDL defines a DAG, there
	 * must be at least one node defined, and all dependencies defined must be
	 * valid and referencing one of the existing nodes. Finally, all files
	 * defined in the Input Sandbox must be available in the provided
	 * {@link AdaptorJDLResources}
	 * 
	 * @param internal
	 *            the parsed info
	 * 
	 * @throws WorkflowValidationException
	 *             The info parsed is not valid
	 */
	private void ValidateParsedInfo(ParsedJDLInfo internal) throws WorkflowValidationException {
		if (internal.jobDescriptionType.equals(JobDescriptionType.Job) && (!internal.jobType.equals(JobType.Normal) && !internal.jobType.equals(JobType.WS)))
			throw new WorkflowValidationException("Only Normal JobType attribute supported");
		if (internal.jobDescriptionType.equals(JobDescriptionType.DAG) && internal.Nodes.size() == 0)
			throw new WorkflowValidationException("No node defined for DAG job");
		if (internal.jobDescriptionType.equals(JobDescriptionType.DAG)) {
			for (Map.Entry<String, ParsedJDLInfo> jobEntry : internal.Nodes.entrySet()) {
				if (!jobEntry.getValue().jobDescriptionType.equals(JobDescriptionType.Job))
					throw new WorkflowValidationException("Only Job Type attribute supported for DAG nodes");
				if (!jobEntry.getValue().jobType.equals(JobType.Normal) && !jobEntry.getValue().jobType.equals(JobType.WS))
					throw new WorkflowValidationException("Only Normal JobType attribute supported for DAG nodes");
			}
			for (Map.Entry<String, List<String>> depEntry : internal.Dependencies.entrySet()) {
				if (!internal.Nodes.containsKey(depEntry.getKey()))
					throw new WorkflowValidationException("Dependency defined for non existing node " + depEntry.getKey());
			}
		}
		for (String insandbx : internal.InSandbox) {
			if (JDLParsingUtils.IsSandboxNameReference(insandbx))
				continue;
			if (!this.Resources.ResourceExists(insandbx))
				throw new WorkflowValidationException("Resource with key " + insandbx + " not attached");
		}
	}

	/**
	 * Construct the workflow to be executed
	 * 
	 * @throws WorkflowInternalErrorException
	 *             An internal error occurred
	 * @throws WorkflowValidationException
	 *             An validation error occurred
	 * @throws WorkflowEnvironmentException
	 *             An error from the environment occurred
	 */
	private void ConstructWorkflow() throws WorkflowInternalErrorException, WorkflowValidationException, WorkflowEnvironmentException {
		IParsedInfo tmpinfo = this.Parser.GetParsedInfo();
		if (!(tmpinfo instanceof ParsedJDLInfo))
			throw new WorkflowInternalErrorException("Unexpected internal plan type");
		ParsedJDLInfo internal = (ParsedJDLInfo) tmpinfo;
		this.ValidateParsedInfo(internal);
		// logger.debug(internal.ToXML());

		// Store the resources that will be need to be moved to the execution
		// nodes
		try {
			this.Resources.StoreResources(this.Hints);
		} catch (Exception ex) {
			throw new WorkflowEnvironmentException("Could not store resources in storage system", ex);
		}

		this.Plan = new ExecutionPlan();
		// Set the maximum running nodes which are considered to be boundaries
		// of the plan
		this.Plan.Config.ConcurrentActionsPerBoundary = internal.MaxRunningNodes;
		this.Plan.Config.RestrictActionTypes.clear();
		if (internal.jobType.equals(JobType.Normal))
			this.Plan.Config.RestrictActionTypes.add(IPlanElement.PlanElementType.Boundary);
		this.Plan.Config.ConnectionCallbackTimeout = 1000 * 60 * 60 * 24; // 24 hours
		this.Plan.Config.ModeOfConnection = ConnectionMode.Callback;
		// Set the mode of connection
		this.Plan.Config.ModeOfConnection = internal.ModeOfConnection;
		this.Plan.EnvHints = this.Hints;
		switch (internal.jobDescriptionType) {
		case Job: {
			// In case the JDL defines a simple job
			SequencePlanElement seq = new SequencePlanElement();
			this.Plan.Root = seq;
			if (internal.jobType.equals(JobType.Normal))
				seq.ElementCollection.add(this.ConstructJobFlow(internal, null, null));
			else if (internal.jobType.equals(JobType.WS))
				seq.ElementCollection.add(this.ConstructWSJobFlow(internal, null, null));
			break;
		}
		case DAG: {
			// In case the JDL defines a DAG of jobs
			switch (internal.ModeOfParsing) {
			case Plan: {
				// In case the parsing mode is plan
				this.Plan.Root = this.ConstructDAGPlanFlow(internal);
				break;
			}
			case Bag: {
				// In case the parsing mode is bag
				this.Plan.Root = this.ConstructDAGBagFlow(internal);
				break;
			}
			default: {
				throw new WorkflowValidationException("Unrecognized parsing mode");
			}
			}
			this.ConnectIntermediateStaging(internal);
			break;
		}
		default: {
			throw new WorkflowValidationException("Unrecognized job description type");
		}
		}
	}

	private List<HostingNode> PrepareMatchmaking(ParsedJDLInfo internal) throws WorkflowEnvironmentException {
		try {
			String commonRequirements = internal.GetCommonRequirements();
			int commonMatches = 0;
			for (String node : internal.Nodes.keySet()) {
				if (internal.GetExtraRequirements(node).trim().equals(""))
					commonMatches++;
			}
			if (commonMatches > 1) {
				logger.debug("Common requirement matchmaking: matches=" + commonMatches + " common requirements=" + commonRequirements);
				List<NodeInfo> matchingNodes = InformationSystem.GetMatchingNodes(internal.Rank, commonRequirements, Hints);
				return new NodeInfo2HostingNodeAdapter().adaptAll(matchingNodes);
			}
			return null;
		} catch (Exception ex) {
			throw new WorkflowEnvironmentException("Could not prepare matchmaking", ex);
		}
	}

	/**
	 * Construct DAG bag flow.
	 * 
	 * @param internal
	 *            the parsed info
	 * @param bConfig
	 *            the boundary config to use in case all nodes need to be
	 *            collocated
	 * 
	 * @return the bag plan element
	 * 
	 * @throws WorkflowEnvironmentException
	 *             An error from the environment occurred
	 * @throws WorkflowValidationException
	 *             A Validation error occurred
	 */
	private BagPlanElement ConstructDAGBagFlow(ParsedJDLInfo internal) throws WorkflowEnvironmentException, WorkflowValidationException {
		// Create a bag element that will terminate when no progress can be made
		BagPlanElement bag = new BagPlanElement();
		bag.TerminationCondition = null;
		bag.TerminateOnNoProgress = true;
		List<HostingNode> matchmaked = PrepareMatchmaking(internal);
		for (Map.Entry<String, ParsedJDLInfo> nfo : internal.Nodes.entrySet()) {
			List<HostingNode> nodeMatchmaked = null;
			if (internal.GetExtraRequirements(nfo.getKey()).equals(""))
				nodeMatchmaked = matchmaked;
			// Create a bag element for each node of the DAG
			BagConditionalElement elem = new BagConditionalElement();
			elem.Executed = false;
			if (internal.jobType.equals(JobType.Normal)) {
				// The execution part of the element is the Boundary describing
				// the execution of the job
				elem.Element = this.ConstructJobFlow(nfo.getValue(), nodeMatchmaked, nfo.getKey());
			} else if (internal.jobType.equals(JobType.WS)) {
				elem.Element = this.ConstructWSJobFlow(nfo.getValue(), nodeMatchmaked, nfo.getKey());
			}
			// The execution condition of the element is that all its
			// dependencies have been completed
			if (internal.Dependencies.containsKey(nfo.getKey())) {
				elem.Condition = new ConditionTree();
				elem.Condition.Root = new ConditionTreeLeaf();
				((ConditionTreeLeaf) elem.Condition.Root).Condition = new BagElementDependencyPlanCondition();
				for (String dep : internal.Dependencies.get(nfo.getKey())) {
					((BagElementDependencyPlanCondition) ((ConditionTreeLeaf) elem.Condition.Root).Condition).DependsOn.add(dep);
				}
			} else
				elem.Condition = null;
			bag.ElementCollection.put(nfo.getKey(), elem);
		}
		return bag;
	}

	/**
	 * Construct DAG plan flow.
	 * 
	 * @param internal
	 *            the parsed info
	 * @param bConfig
	 *            the boundary config to use in case all nodes need to be
	 *            collocated
	 * 
	 * @return the sequence plan element
	 * 
	 * @throws WorkflowEnvironmentException
	 *             An error from the environment occurred
	 * @throws WorkflowValidationException
	 *             A Validation error occurred
	 */
	private SequencePlanElement ConstructDAGPlanFlow(ParsedJDLInfo internal) throws WorkflowEnvironmentException,
			WorkflowValidationException {
		SequencePlanElement seq = new SequencePlanElement();
		// Construct the dependency graph
		ArrayList<ArrayList<String>> levels = this.CreateDependencyGraph(internal.Nodes, internal.Dependencies);
		List<HostingNode> matchmaked = PrepareMatchmaking(internal);
		// For every horizontal of the graph
		for (ArrayList<String> level : levels) {
			StringBuilder buf = new StringBuilder();
			FlowPlanElement flw = null;
			// If more than one nodes are contained in the same level, they can
			// be executed in parallel
			if (level.size() > 1)
				flw = new FlowPlanElement();
			for (String l : level) {
				buf.append(l + " ");
				List<HostingNode> nodeMatchmaked = null;
				if (internal.GetExtraRequirements(l).equals(""))
					nodeMatchmaked = matchmaked;
				if (internal.jobType.equals(JobType.Normal)) {
					if (flw != null)
						flw.ElementCollection.add(this.ConstructJobFlow(internal.Nodes.get(l), nodeMatchmaked, l));
					else
						seq.ElementCollection.add(this.ConstructJobFlow(internal.Nodes.get(l), nodeMatchmaked, l));
				} else {
					if (flw != null)
						flw.ElementCollection.add(this.ConstructWSJobFlow(internal.Nodes.get(l), nodeMatchmaked, l));
					else
						seq.ElementCollection.add(this.ConstructWSJobFlow(internal.Nodes.get(l), nodeMatchmaked, l));
				}

			}
			if (flw != null)
				seq.ElementCollection.add(flw);
			logger.debug("ConstructDAGPlanFlow: " + buf.toString());
		}
		return seq;
	}

	/**
	 * Creates the dependency graph. A node is place in one level of the
	 * dependency graph if all its dependencies have been executed in the
	 * previous level of the graph
	 * 
	 * @param Nodes
	 *            the nodes defined in the DAG
	 * @param Dependencies
	 *            the dependencies that are defined for these nodes
	 * 
	 * @return the dependency graph defined as a series of levels, where each
	 *         level must be executed sequentially after the previous one
	 *         completed, and all nodes in the same level can be executed in
	 *         parallel
	 * 
	 * @throws WorkflowValidationException
	 *             A validation error occurred
	 */
	private ArrayList<ArrayList<String>> CreateDependencyGraph(Map<String, ParsedJDLInfo> Nodes, Map<String, List<String>> Dependencies)
			throws WorkflowValidationException {
		HashSet<String> CanRun = new HashSet<String>();
		HashSet<String> LevelCanRun = new HashSet<String>();
		ArrayList<ArrayList<String>> levels = new ArrayList<ArrayList<String>>();
		while (CanRun.size() < Nodes.size()) {
			ArrayList<String> level = new ArrayList<String>();
			for (Map.Entry<String, ParsedJDLInfo> node : Nodes.entrySet()) {
				if (CanRun.contains(node.getKey()))
					continue;
				if (!Dependencies.containsKey(node.getKey())) {
					LevelCanRun.add(node.getKey());
					// CanRun.add(node.getKey());
					level.add(node.getKey());
				} else {
					boolean DepsDone = true;
					for (String dep : Dependencies.get(node.getKey())) {
						if (!CanRun.contains(dep)) {
							DepsDone = false;
							break;
						}
					}
					if (DepsDone) {
						LevelCanRun.add(node.getKey());
						// CanRun.add(node.getKey());
						level.add(node.getKey());
					}
				}
			}
			CanRun.addAll(LevelCanRun);
			LevelCanRun.clear();
			levels.add(level);
			if (level.size() == 0)
				throw new WorkflowValidationException("Cannot determine graph execution order");
		}
		return levels;
	}

	/**
	 * Construct the {@link BoundaryPlanElement} that describes the execution of
	 * a single job. The resources of the Input Sandbox are retrieved, the
	 * Boundary Config if provided is used otherwise a new node is picked and
	 * finally the {@link ShellPlanElement} that describes the execution is
	 * constructed
	 * 
	 * @param internal
	 *            the parsed plan
	 * @param bConfig
	 *            the boundary config
	 * @param NodeName
	 *            the node name
	 * 
	 * @return the boundary plan element constructed
	 * 
	 * @throws WorkflowEnvironmentException
	 *             An error of the environment occurred
	 * @throws WorkflowValidationException
	 *             A validation error occurred
	 */
	private BoundaryPlanElement ConstructJobFlow(ParsedJDLInfo internal, List<HostingNode> matchmaked, String NodeName)
			throws WorkflowEnvironmentException, WorkflowValidationException {
		BoundaryPlanElement boundaryElement = new BoundaryPlanElement();
		boundaryElement.SetName(NodeName);

		boundaryElement.Isolation = new BoundaryIsolationInfo();
		boundaryElement.Isolation.CleanUp = true;
		boundaryElement.Isolation.Isolate = true;
		boundaryElement.Isolation.BaseDir = new SimpleInOutParameter();
		NamedDataType ndtIsolationBaseDirParameter = new NamedDataType();
		ndtIsolationBaseDirParameter.IsAvailable = false;
		ndtIsolationBaseDirParameter.Name = UUID.randomUUID().toString();
		logger.debug("Boundary isolation dir is " + ndtIsolationBaseDirParameter.Name);
		ndtIsolationBaseDirParameter.Token = ndtIsolationBaseDirParameter.Name;
		ndtIsolationBaseDirParameter.Value = new DataTypeString();
		this.Plan.Variables.Add(ndtIsolationBaseDirParameter);
		((SimpleInOutParameter) boundaryElement.Isolation.BaseDir).VariableName = ndtIsolationBaseDirParameter.Name;

		if (internal.NodesCollocation)
			boundaryElement.Config = this.GetBoundaryConfig(internal.Rank, internal.Requirements, null);
		else
			boundaryElement.Config = this.GetBoundaryConfig(internal.Rank, internal.Requirements, matchmaked);

		SequencePlanElement seqRemote = new SequencePlanElement();
		FlowPlanElement flowRemote = new FlowPlanElement();
		boundaryElement.Root = seqRemote;

		int counter = 0; // current file transfer

		for (AttachedJDLResource att : this.Resources.Resources) {
			if (!internal.InSandbox.contains(att.Key) || att.TypeOfResource != ResourceType.InData)
				continue;

			flowRemote.ElementCollection.add(this.CreateInputSandboxRetrieveElement(att, internal));
			if (++counter == MAX_TRANSFERS) {
				seqRemote.ElementCollection.add(flowRemote);
				flowRemote = new FlowPlanElement();
				counter = 0;
			}
		}
		for (String SandboxName : internal.InSandbox) {
			if (JDLParsingUtils.IsSandboxNameReference(SandboxName)) {
				flowRemote.ElementCollection.add(this.CreateInternalStagingRetrieveElement(SandboxName));
				if (++counter == MAX_TRANSFERS) {
					seqRemote.ElementCollection.add(flowRemote);
					flowRemote = new FlowPlanElement();
					counter = 0;
				}
			}
		}

		if (counter > 0) {
			seqRemote.ElementCollection.add(flowRemote);
			flowRemote = new FlowPlanElement();
		}

		TryCatchFinallyPlanElement tcf = new TryCatchFinallyPlanElement();
		flowRemote.ElementCollection.add(tcf);
		seqRemote.ElementCollection.add(flowRemote);
		tcf.TryFlow = this.CreateExecutableElement(internal);
		tcf.CatchFlows.clear();
		tcf.FinallyFlow = new SequencePlanElement();

		FileTransferPlanElement outfts = null;
		FileTransferPlanElement errfts = null;
		List<FileTransferPlanElement> otherfts = new ArrayList<FileTransferPlanElement>();

		int count = 0;
		for (String outbox : internal.OutSandbox) {
			if (internal.Output != null && internal.Output.equals(outbox)) {
				outfts = this.CreateOutputSandboxStoreElement(outbox, NodeName, count);
				if (this.Resources.ResourceExists(outbox) && this.Resources.GetResource(outbox).ResourceLocationType == AttachedResourceType.Reference) {
					boolean slashFound = this.Resources.GetResource(outbox).Value.charAt(this.Resources.GetResource(outbox).Value.length() - 1) == '/';
					outfts.OutputStoreMode = FileTransferPlanElement.StoreMode.Url;
					// outfts.StoreUrlLocation =
					// this.Resources.GetResource(outbox).Value + "/" +
					// (this.ExecutionId != null ? this.ExecutionId + "/": "" )
					// + outbox;
					outfts.StoreUrlLocation = this.Resources.GetResource(outbox).Value + (slashFound ? "" : "/")
							+ (this.ExecutionId != null ? this.ExecutionId + "." + (NodeName != null ? NodeName + "." : "") : "") + outbox;
					outfts.accessInfo.port = this.Resources.GetResource(outbox).accessInfo.port;
					outfts.accessInfo.userId = this.Resources.GetResource(outbox).accessInfo.userId;
					outfts.accessInfo.password = this.Resources.GetResource(outbox).accessInfo.password;
				}
			} else if (internal.Error != null && internal.Error.equals(outbox)) {
				errfts = this.CreateOutputSandboxStoreElement(outbox, NodeName, count);
				if (this.Resources.ResourceExists(outbox) && this.Resources.GetResource(outbox).ResourceLocationType == AttachedResourceType.Reference) {
					boolean slashFound = this.Resources.GetResource(outbox).Value.charAt(this.Resources.GetResource(outbox).Value.length() - 1) == '/';
					errfts.OutputStoreMode = FileTransferPlanElement.StoreMode.Url;
					// errfts.StoreUrlLocation =
					// this.Resources.GetResource(outbox).Value + "/" +
					// (this.ExecutionId != null ? this.ExecutionId + "/" : "")
					// + outbox;
					errfts.StoreUrlLocation = this.Resources.GetResource(outbox).Value + (slashFound ? "" : "/")
							+ (this.ExecutionId != null ? this.ExecutionId + "." + (NodeName != null ? NodeName + "." : "") : "") + outbox;
					errfts.accessInfo.port = this.Resources.GetResource(outbox).accessInfo.port;
					errfts.accessInfo.userId = this.Resources.GetResource(outbox).accessInfo.userId;
					errfts.accessInfo.password = this.Resources.GetResource(outbox).accessInfo.password;
				}
			} else {
				if (this.Resources.ResourceExists(outbox) && this.Resources.GetResource(outbox).TypeOfResource == ResourceType.OutData) {
					FileTransferPlanElement fts = this.CreateOutputSandboxStoreElement(outbox, NodeName, count);
					if (this.Resources.GetResource(outbox).ResourceLocationType == AttachedResourceType.Reference) {
						fts.OutputStoreMode = FileTransferPlanElement.StoreMode.Url;
						// fts.StoreUrlLocation =
						// this.Resources.GetResource(outbox).Value + "/" +
						// (this.ExecutionId != null ? this.ExecutionId + "/":
						// "" + outbox);
						boolean slashFound = this.Resources.GetResource(outbox).Value.charAt(this.Resources.GetResource(outbox).Value.length() - 1) == '/';
						fts.StoreUrlLocation = this.Resources.GetResource(outbox).Value + (slashFound ? "" : "/")
								+ (this.ExecutionId != null ? this.ExecutionId + "." + (NodeName != null ? NodeName + "." : "") : "") + outbox;
						fts.accessInfo.port = this.Resources.GetResource(outbox).accessInfo.port;
						fts.accessInfo.userId = this.Resources.GetResource(outbox).accessInfo.userId;
						fts.accessInfo.password = this.Resources.GetResource(outbox).accessInfo.password;
					}
					otherfts.add(fts);
				}
			}

			count += 1;
		}
		if (outfts != null)
		{
			((SequencePlanElement) tcf.FinallyFlow).ElementCollection.add(this.ChockError(outfts));
		}
		if (errfts != null)
			((SequencePlanElement) tcf.FinallyFlow).ElementCollection.add(this.ChockError(errfts));
		List<TryCatchFinallyPlanElement> chokedElements = new ArrayList<TryCatchFinallyPlanElement>();
		for (FileTransferPlanElement otherft : otherfts)
			chokedElements.add(this.ChockError(otherft));
		((SequencePlanElement) tcf.FinallyFlow).ElementCollection.addAll(chokedElements);
		if (((SequencePlanElement) tcf.FinallyFlow).ElementCollection.size() == 0)
			tcf.FinallyFlow = null;
		return boundaryElement;
	}

	
	private SequencePlanElement ConstructWSJobFlow(ParsedJDLInfo internal, List<HostingNode> matchmaked, String NodeName)
			throws WorkflowEnvironmentException, WorkflowValidationException {
		NamedDataType ndtIsolationBaseDirParameter = new NamedDataType();
		ndtIsolationBaseDirParameter.IsAvailable = false;
		ndtIsolationBaseDirParameter.Name = UUID.randomUUID().toString();
		logger.debug("Boundary isolation dir is " + ndtIsolationBaseDirParameter.Name);
		ndtIsolationBaseDirParameter.Token = ndtIsolationBaseDirParameter.Name;
		ndtIsolationBaseDirParameter.Value = new DataTypeString();
		this.Plan.Variables.Add(ndtIsolationBaseDirParameter);
		SequencePlanElement seqRemote = new SequencePlanElement();
		seqRemote.SetName(NodeName);
		for (AttachedJDLResource att : this.Resources.Resources) {
			if (!internal.InSandbox.contains(att.Key) || att.TypeOfResource != ResourceType.InData)
				continue;
			seqRemote.ElementCollection.add(this.CreateInputSandboxRetrieveElement(att, internal));
		}
		for (String SandboxName : internal.InSandbox) {
			if (JDLParsingUtils.IsSandboxNameReference(SandboxName)) {
				seqRemote.ElementCollection.add(this.CreateInternalStagingRetrieveElement(SandboxName));
			}
		}
		TryCatchFinallyPlanElement tcf = new TryCatchFinallyPlanElement();
		seqRemote.ElementCollection.add(tcf);
		try {
			tcf.TryFlow = this.CreateWSSOAPElement(internal);
		} catch (ExecutionValidationException e) {
			throw new WorkflowEnvironmentException();
		}
		tcf.CatchFlows.clear();
		tcf.FinallyFlow = new SequencePlanElement();

		if (((SequencePlanElement) tcf.FinallyFlow).ElementCollection.size() == 0)
			tcf.FinallyFlow = null;
		return seqRemote;
	}

	private TryCatchFinallyPlanElement ChockError(IPlanElement element) {
		TryCatchFinallyPlanElement tcf = new TryCatchFinallyPlanElement();
		tcf.TryFlow = element;
		CatchElement c = new CatchElement();
		c.Error = null;
		c.Rethrow = false;
		c.Root = null;
		tcf.CatchFlows.add(c);
		tcf.FinallyFlow = null;
		return tcf;
	}

	/**
	 * Retrieves the Boundary config to use when creating a
	 * {@link BoundaryPlanElement} based on the rank and requirements function
	 * if provided. Otherwise, a random node is picked
	 * 
	 * @param Rank
	 *            the rank function
	 * @param Requirements
	 *            the requirements function
	 * 
	 * @return the boundary config created
	 * 
	 * @throws WorkflowEnvironmentException
	 *             An error in the environment occurred
	 */
	private BoundaryConfig GetBoundaryConfig(String Rank, String Requirements, List<HostingNode> matchmaked) throws WorkflowEnvironmentException {
		NodeInfo nodeToUse = null;
		String hostname = null;
		int port = -1;
		try {
			logger.debug("Retrieving matching node: Rank=" + Rank + " Requirements=" + Requirements);
			if (matchmaked != null) {
				HostingNode n = this.NodeSelector.selectNode(matchmaked);
				hostname = n.getPropertyByName("hostname");
				port = Integer.parseInt(n.getPropertyByName("pe2ng.port"));
			} else {
				nodeToUse = InformationSystem.GetMatchingNode(Rank, Requirements, this.NodeSelector, this.Hints);
				if (nodeToUse == null)
					throw new WorkflowEnvironmentException("Could not find appropriate node to host execution");
				hostname = nodeToUse.getExtension("hostname");
				port = Integer.parseInt(nodeToUse.getExtension("pe2ng.port"));
			}
			logger.info("Selected Execution Engine: " + hostname + ":" + port + (matchmaked != null ? " (used common requirements)" : ""));

		} catch (EnvironmentInformationSystemException ex) {
			throw new WorkflowEnvironmentException("Could not retrieve appropriate node to host execution", ex);
		}
		BoundaryConfig Config = new BoundaryConfig();
		Config.HostName = hostname;
		Config.Port = port;
		Config.NozzleConfig = new TCPServerNozzleConfig(false, 0);
		return Config;
	}

	/**
	 * Creates an element that stores the product of an execution unit defined
	 * in its Output Sandbox
	 * 
	 * @param SandboxName
	 *            the sandbox name
	 * @param NodeName
	 *            the node name
	 * 
	 * @return the file transfer plan element
	 * 
	 * @throws WorkflowValidationException
	 *             A workflow validation error occurred
	 */
	private FileTransferPlanElement CreateOutputSandboxStoreElement(String SandboxName, String NodeName, int SandboxIndex) throws WorkflowValidationException {
		OutputSandboxJDLResource outRes = new OutputSandboxJDLResource();
		outRes.NodeName = NodeName;
		outRes.SandboxName = SandboxName;
		outRes.SandboxIndex = SandboxIndex;
		FileTransferPlanElement ftr = new FileTransferPlanElement();
		ftr.Direction = TransferDirection.Store;
		SimpleInParameter fileToStoreParameter = new SimpleInParameter();
		NamedDataType ndtFileToStoreParameter = new NamedDataType();
		ndtFileToStoreParameter.IsAvailable = true;
		ndtFileToStoreParameter.Name = UUID.randomUUID().toString();
		ndtFileToStoreParameter.Token = ndtFileToStoreParameter.Name;
		ndtFileToStoreParameter.Value = new DataTypeString();
		try {
			((DataTypeString) ndtFileToStoreParameter.Value).SetValue(SandboxName);
		} catch (Exception ex) {
			throw new WorkflowValidationException("Could not create execution plan", ex);
		}
		this.Plan.Variables.Add(ndtFileToStoreParameter);
		fileToStoreParameter.VariableName = ndtFileToStoreParameter.Name;
		ftr.Input = fileToStoreParameter;
		SimpleOutParameter fileStoredParameter = new SimpleOutParameter();
		NamedDataType ndtFileStoredParameter = new NamedDataType();
		ndtFileStoredParameter.IsAvailable = false;
		ndtFileStoredParameter.Name = UUID.randomUUID().toString();
		ndtFileStoredParameter.Token = SandboxName;
		ndtFileStoredParameter.Value = new DataTypeString();
		this.Plan.Variables.Add(ndtFileStoredParameter);
		outRes.VariableID = ndtFileStoredParameter.Name;
		outRes.VariableID = ndtFileStoredParameter.Name;
		fileStoredParameter.VariableName = ndtFileStoredParameter.Name;
		ftr.Output = fileStoredParameter;
		ftr.IsExecutable = false;
		ftr.MoveTo = null;
		this.OutputResources.add(outRes);
		return ftr;
	}

	/**
	 * Creates the executable element wrapped in a {@link ShellPlanElement}.
	 * This is the executable unit wrapping the defined executable of a job. The
	 * arguments defined for the job are split to multiple arguments using the
	 * space character as a delimiter. If a retry count has been defined it is
	 * also used at this level.
	 * 
	 * @param internal
	 *            the parsed info
	 * 
	 * @return the shell plan element
	 * 
	 * @throws WorkflowValidationException
	 *             A validation error occurred
	 */
	private ShellPlanElement CreateExecutableElement(ParsedJDLInfo internal) throws WorkflowValidationException {
		ShellPlanElement shell = new ShellPlanElement();
		logger.info("Shell plan element arguments: " + internal.Arguments);
		if (internal.Arguments != null) {
			String[] args = internal.Arguments.trim().split("\\s");
			for (String arg : args) {
				SimpleInParameter argParameter = new SimpleInParameter();
				NamedDataType ndtArgParameter = new NamedDataType();
				ndtArgParameter.IsAvailable = true;
				ndtArgParameter.Name = UUID.randomUUID().toString();
				ndtArgParameter.Token = ndtArgParameter.Name;
				ndtArgParameter.Value = new DataTypeString();
				try {
					((DataTypeString) ndtArgParameter.Value).SetValue(arg);
				} catch (Exception ex) {
					throw new WorkflowValidationException("Could not create execution plan", ex);
				}
				argParameter.VariableName = ndtArgParameter.Name;
				this.Plan.Variables.Add(ndtArgParameter);
				shell.ArgumentParameters.add(new AttributedInputParameter(argParameter));
			}
		}
		shell.Command = internal.Executable;
		if (internal.Error != null) {
			SimpleInOutParameter stdErrParameter = new SimpleInOutParameter();
			NamedDataType ndtStdErrParameter = new NamedDataType();
			ndtStdErrParameter.IsAvailable = true;
			ndtStdErrParameter.Name = UUID.randomUUID().toString();
			ndtStdErrParameter.Token = ndtStdErrParameter.Name;
			ndtStdErrParameter.Value = new DataTypeString();
			try {
				((DataTypeString) ndtStdErrParameter.Value).SetValue(internal.Error);
			} catch (Exception ex) {
				throw new WorkflowValidationException("Could not create execution plan", ex);
			}
			stdErrParameter.VariableName = ndtStdErrParameter.Name;
			this.Plan.Variables.Add(ndtStdErrParameter);
			shell.StdErrParameter = stdErrParameter;
			shell.StdErrIsFile = true;
		}
		SimpleOutParameter exitValueParameter = new SimpleOutParameter();
		NamedDataType ndtExitValueParameter = new NamedDataType();
		ndtExitValueParameter.IsAvailable = false;
		ndtExitValueParameter.Name = UUID.randomUUID().toString();
		ndtExitValueParameter.Token = ndtExitValueParameter.Name;
		ndtExitValueParameter.Value = new DataTypeString();
		exitValueParameter.VariableName = ndtExitValueParameter.Name;
		this.Plan.Variables.Add(ndtExitValueParameter);
		shell.StdExitValueParameter = exitValueParameter;
		if (internal.Input != null) {
			SimpleInParameter stdInParameter = new SimpleInParameter();
			NamedDataType ndtStdInParameter = new NamedDataType();
			ndtStdInParameter.IsAvailable = true;
			ndtStdInParameter.Name = UUID.randomUUID().toString();
			ndtStdInParameter.Token = ndtStdInParameter.Name;
			ndtStdInParameter.Value = new DataTypeString();
			try {
				((DataTypeString) ndtStdInParameter.Value).SetValue(internal.Input);
			} catch (Exception ex) {
				throw new WorkflowValidationException("Could not create execution plan", ex);
			}
			stdInParameter.VariableName = ndtStdInParameter.Name;
			this.Plan.Variables.Add(ndtStdInParameter);
			shell.StdInParameter = stdInParameter;
			shell.StdInIsFile = true;
		}
		if (internal.Output != null) {
			SimpleInOutParameter stdOutParameter = new SimpleInOutParameter();
			NamedDataType ndtStdOutParameter = new NamedDataType();
			ndtStdOutParameter.IsAvailable = true;
			ndtStdOutParameter.Name = UUID.randomUUID().toString();
			ndtStdOutParameter.Token = ndtStdOutParameter.Name;
			ndtStdOutParameter.Value = new DataTypeString();
			try {
				((DataTypeString) ndtStdOutParameter.Value).SetValue(internal.Output);
			} catch (Exception ex) {
				throw new WorkflowValidationException("Could not create execution plan", ex);
			}
			stdOutParameter.VariableName = ndtStdOutParameter.Name;
			this.Plan.Variables.Add(ndtStdOutParameter);
			shell.StdOutParameter = stdOutParameter;
			shell.StdOutIsFile = true;
		}
		shell.ExitCodeErrors.clear();
		shell.Triggers.clear();
		if (internal.RetryCount > 0) {
			ContingencyTrigger trigg = new ContingencyTrigger();
			trigg.IsFullNameOfError = false;
			trigg.TriggeringError = null;
			trigg.Reaction = new ContingencyReactionRetry();
			((ContingencyReactionRetry) trigg.Reaction).NumberOfRetries = internal.RetryCount;
			((ContingencyReactionRetry) trigg.Reaction).RetryInterval = (internal.RetryInterval > 0 ? internal.RetryInterval : ParsedJDLInfo.DefaultRetryInterval);
			shell.Triggers.add(trigg);
		}
		shell.Environment.clear();
		for (EnvironmentKeyValue envkv : internal.Environment)
			shell.Environment.add(envkv);
		return shell;
	}

	/**
	 * Creates the executable element wrapped in a {@link WSSOAPPlanElement}.
	 * This is the executable unit wrapping the defined executable of a job. The
	 * arguments defined for the job are split to multiple arguments using the
	 * space character as a delimiter. If a retry count has been defined it is
	 * also used at this level.
	 * 
	 * @param internal
	 *            the parsed info
	 * 
	 * @return the ws soap plan element
	 * 
	 * @throws WorkflowValidationException
	 *             A validation error occurred
	 * @throws ExecutionValidationException
	 */
	private WSSOAPPlanElement CreateWSSOAPElement(ParsedJDLInfo internal) throws WorkflowValidationException, ExecutionValidationException {
		WSSOAPPlanElement spe = new WSSOAPPlanElement();
		logger.info("WS Soap plan element arguments: " + internal.Arguments);
		String[] args = null;
		if (internal.Arguments != null) {
			logger.info("Arguments: "+internal.Arguments.trim());
			args = internal.Arguments.trim().split("\\s+");
		}
		
		// create Options object
		Options options = new Options();
		options.addOption("o", true, "output file");
		options.addOption(OptionBuilder.hasArgs().withValueSeparator(':').withDescription("input files and input variables").create("i"));
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e) {
			logger.error("Unable to parse the WS arguments",e);
		}
		if(cmd.hasOption('i'))
			logger.info("InputFiles and Variables are: "+Arrays.asList(cmd.getOptionValues('i')));
		if(cmd.hasOption('o'))
			logger.info("OutputFile is: "+cmd.getOptionValue('o'));
		Map<String,String> wsVars = new HashMap<String, String>();
		
		if(this.Hints.HintExists("GCubeActionScope"))
		{
			wsVars.put(SOAPBuilder.SCOPE, this.Hints.GetHint("GCubeActionScope").Hint.Payload);
		}
		
		if(cmd.hasOption('i'))
		{
			String inputs[] = cmd.getOptionValues('i');
			if(inputs.length%2 != 0)
				throw new IllegalArgumentException("Wrong input format, length is not divided by 2, "+inputs.length+": "+Arrays.asList(inputs));
			for(int i=0;i<inputs.length;i+=2)
			{
				if(this.WSVariables.containsKey(inputs[i]))
				{
					logger.info("Added to wsVars "+inputs[i]);
					wsVars.put(inputs[i+1], this.WSVariables.get(inputs[i]));
				}
			}
		}
		
		// passing the envelope to the wrapper for parsing
		ServiceWrapper wrapper = new ServiceWrapper(this.Resources.GetResource(internal.InSandbox.get(0)).Value, wsVars);
		WSSOAPCall queryCall = new WSSOAPCall();

		queryCall.Order = 0;
		queryCall.MethodName = wrapper.getActionOperation();
		queryCall.ActionURN = wrapper.getActionURN();
		FilteredOutParameter queryResult = new FilteredOutParameter();
		queryResult.UpdateVariableName = wrapper.getInvocationResult();
		ParameterXPathFilter extractRSLocatorFilter = new ParameterXPathFilter();
		extractRSLocatorFilter.FilteredVariableName = wrapper.getInvocationResult();
		extractRSLocatorFilter.Order = 0;
		extractRSLocatorFilter.OutputQueryResultType = OutputResultType.String;
		extractRSLocatorFilter.StoreOutput = true;
		extractRSLocatorFilter.StoreOutputVariableName = wrapper.getOutputLocator();
		extractRSLocatorFilter.FilterExpressionVariableName = wrapper.getOutputLocatorExtractionExpressionVariable();
		queryResult.Filters.add(extractRSLocatorFilter);
		queryCall.OutputParameter = queryResult;

		WSSOAPArgument queryEnvelop = wrapper.getQueryEnvelopeArgument();
		queryCall.ArgumentList.add(queryEnvelop);
		queryCall.ExecutionContextToken = "[ExecutionEngineContext]";

		spe.Calls.add(queryCall);

		try {
			spe.ServiceEndPoint = (IInputOutputParameter) ParameterUtils.GetSimpleParameter(ParameterDirectionType.InOut, wrapper.getServiceEndpoint());
		} catch (ExecutionValidationException e) {
			logger.error("Error while creating the service endpoint", e);
		}

		// adding variables to plan
		wrapper.addVariablesToPlan(this.Plan);

		if(cmd.hasOption('o'))
		{
			logger.info("Addedtowsvars:"+cmd.getOptionValue("o")+" "+wrapper.getOutputLocator());
			this.WSVariables.put(cmd.getOptionValue("o"), wrapper.getOutputLocator());
		}
		// setting retry trigger
		spe.Triggers.clear();
		ContingencyTrigger trigg = new ContingencyTrigger();
		trigg.IsFullNameOfError = false;
		trigg.TriggeringError = null;
		trigg.Reaction = new ContingencyReactionRetry();
		((ContingencyReactionRetry) trigg.Reaction).NumberOfRetries = internal.RetryCount;
		((ContingencyReactionRetry) trigg.Reaction).RetryInterval = (internal.RetryInterval > 0 ? internal.RetryInterval : ParsedJDLInfo.DefaultRetryInterval);
		spe.Triggers.add(trigg);
		return spe;
	}

	private FileTransferPlanElement CreateInternalStagingRetrieveElement(String SandboxName) throws WorkflowValidationException {
		FileTransferPlanElement ftr = new FileTransferPlanElement();
		if (JDLParsingUtils.IsSandboxNameReference(SandboxName)) {
			if (!this.Staging.containsKey(SandboxName))
				this.Staging.put(SandboxName, new HashSet<FileTransferPlanElement>());
			this.Staging.get(SandboxName).add(ftr);
			return ftr;
		}
		throw new WorkflowValidationException("Provided sandbox name is not of root reference");
	}

	/**
	 * Creates the input sandbox retrieve element to retrieve from storage
	 * system the files that are defined in the input sandbox of a job
	 * 
	 * @param attachment
	 *            the attachment that describes the resource
	 * @param internal
	 *            the parsed info
	 * 
	 * @return the file transfer plan element
	 * 
	 * @throws WorkflowValidationException
	 *             A validation error occurred
	 */
	private FileTransferPlanElement CreateInputSandboxRetrieveElement(AttachedJDLResource attachment, ParsedJDLInfo internal)
			throws WorkflowValidationException {
		FileTransferPlanElement ftr = new FileTransferPlanElement();
		if (JDLParsingUtils.IsSandboxNameReference(attachment.Key)) {
			throw new WorkflowValidationException("Provided sandbox name is of root reference");
		}
		ftr.Direction = TransferDirection.Retrieve;
		ftr.Input = new SimpleInParameter();
		if (attachment.Key.equals(internal.Executable))
			ftr.IsExecutable = true;
		NamedDataType ndtAttachment = new NamedDataType();
		ndtAttachment.IsAvailable = true;
		ndtAttachment.Name = UUID.randomUUID().toString();
		ndtAttachment.Token = ndtAttachment.Name;
		ndtAttachment.Value = new DataTypeString();
		this.Plan.Variables.Add(ndtAttachment);
		try {
			((DataTypeString) ndtAttachment.Value).SetValue(attachment.StorageSystemID);
		} catch (Exception ex) {
			throw new WorkflowValidationException("Could not create execution plan", ex);
		}
		((SimpleInParameter) ftr.Input).VariableName = ndtAttachment.Name;
		ftr.Output = new SimpleOutParameter();
		NamedDataType ndtRetrievedAttachment = new NamedDataType();
		ndtRetrievedAttachment.IsAvailable = false;
		ndtRetrievedAttachment.Name = UUID.randomUUID().toString();
		ndtRetrievedAttachment.Token = ndtRetrievedAttachment.Name;
		ndtRetrievedAttachment.Value = new DataTypeString();
		this.Plan.Variables.Add(ndtRetrievedAttachment);
		((SimpleOutParameter) ftr.Output).VariableName = ndtRetrievedAttachment.Name;
		ftr.MoveTo = new SimpleInParameter();
		NamedDataType ndtRename = new NamedDataType();
		ndtRename.IsAvailable = true;
		ndtRename.Name = UUID.randomUUID().toString();
		ndtRename.Token = ndtRename.Name;
		ndtRename.Value = new DataTypeString();
		String RenameTo = attachment.Key;
		if (RenameTo == null || RenameTo.trim().length() == 0)
			throw new WorkflowValidationException("Defined resource name is not valid");
		try {
			((DataTypeString) ndtRename.Value).SetValue(RenameTo);
		} catch (Exception ex) {
			throw new WorkflowValidationException("Could not create execution plan", ex);
		}
		this.Plan.Variables.Add(ndtRename);
		((SimpleInParameter) ftr.MoveTo).VariableName = ndtRename.Name;
		return ftr;
	}

	private void ConnectIntermediateStaging(ParsedJDLInfo internal) throws WorkflowValidationException {
		for (Map.Entry<String, HashSet<FileTransferPlanElement>> entry : this.Staging.entrySet()) {
			String nodeName = null;
			int sandboxIndex = 0;
			try {
				nodeName = entry.getKey().substring("root.nodes.".length(), entry.getKey().indexOf(".", "root.nodes.".length()));
				sandboxIndex = Integer.parseInt(entry.getKey().substring(entry.getKey().indexOf("[") + 1, entry.getKey().indexOf("]")));
			} catch (Exception ex) {
				throw new WorkflowValidationException("reference input sandbox element not in valid format", ex);
			}
			for (IOutputResource outres : this.OutputResources) {
				if (((OutputSandboxJDLResource) outres).NodeName.equals(nodeName) && ((OutputSandboxJDLResource) outres).SandboxIndex == sandboxIndex) {
					for (FileTransferPlanElement ftr : entry.getValue()) {
						ftr.Direction = TransferDirection.Retrieve;
						ftr.Input = new SimpleInParameter();
						if (((OutputSandboxJDLResource) outres).SandboxName.equals(internal.Executable))
							ftr.IsExecutable = true;
						((SimpleInParameter) ftr.Input).VariableName = ((OutputSandboxJDLResource) outres).VariableID;
						ftr.Output = new SimpleOutParameter();
						NamedDataType ndtRetrievedAttachment = new NamedDataType();
						ndtRetrievedAttachment.IsAvailable = false;
						ndtRetrievedAttachment.Name = UUID.randomUUID().toString();
						ndtRetrievedAttachment.Token = ndtRetrievedAttachment.Name;
						ndtRetrievedAttachment.Value = new DataTypeString();
						this.Plan.Variables.Add(ndtRetrievedAttachment);
						((SimpleOutParameter) ftr.Output).VariableName = ndtRetrievedAttachment.Name;
						ftr.MoveTo = new SimpleInParameter();
						NamedDataType ndtRename = new NamedDataType();
						ndtRename.IsAvailable = true;
						ndtRename.Name = UUID.randomUUID().toString();
						ndtRename.Token = ndtRename.Name;
						ndtRename.Value = new DataTypeString();
						String RenameTo = ((OutputSandboxJDLResource) outres).SandboxName;
						if (RenameTo == null || RenameTo.trim().length() == 0)
							throw new WorkflowValidationException("Defined resource name is not valid");
						try {
							((DataTypeString) ndtRename.Value).SetValue(RenameTo);
						} catch (Exception ex) {
							throw new WorkflowValidationException("Could not create execution plan", ex);
						}
						this.Plan.Variables.Add(ndtRename);
						((SimpleInParameter) ftr.MoveTo).VariableName = ndtRename.Name;
					}
				}
			}
		}
	}
}
