package gr.uoa.di.madgik.workflow.adaptor.search;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.LocalOnlyPolicy;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.NodeAssignmentPolicy;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.SingleRemoteNodePolicy;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.random.RandomNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.ru.LRUNodeSelector;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FlowPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionServer;
import gr.uoa.di.madgik.rr.element.execution.RRExecutionServer2HnAdapter;
import gr.uoa.di.madgik.rr.element.infra.HostingNode;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceService;
import gr.uoa.di.madgik.rr.element.search.index.FTIndexService;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSourceService;
import gr.uoa.di.madgik.rr.element.search.index.SruConsumerService;
import gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.search.analyzer.SearchPlanAnalyzer;
import gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment.DataSourceNodeAssignmentNode;
import gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment.NodeAssignmentNode;
import gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment.NodeAssignmentTree;
import gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment.OperatorNodeAssignmentNode;
import gr.uoa.di.madgik.workflow.adaptor.search.rewriter.SearchPlanRewriter;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.NodeExecutionInfo;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.OutputVariableNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.WrapperNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing.ExceptElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing.JoinElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing.MergeElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.elementconstructors.processing.ProcessingElementConstructor;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.FunctionalityWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.DataSourceWrapper;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.DataSourceWrapperFactory;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.DataSourceWrapperFactoryConfig;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.ProcessingWrapper;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory.DirectoryEntryType;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class WorkflowSearchAdaptor implements IWorkflowAdaptor, Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkflowSearchAdaptor.class);
	
	/** The search plan from which an execution plan will be created. */
	private PlanNode searchPlan = null;
	
	private EnvHintCollection hints = null;
	
	/** The constructed execution Plan. */
	private ExecutionPlan Plan=null;
	
	/** The id of the executed Plan. */
	private String executionId=null;
	
	private WrapperNode wrapperTree = null;
	
	private OutputVariableNode ioVariableNodes = null;
	
	private transient ExecutionHandle Handle = null;
	
	/** The node assignment policy which will be used to assign operator execution tasks to nodes. */
	private transient NodeAssignmentPolicy nodeAssignmentPolicy = new SingleRemoteNodePolicy(new RandomNodeSelector());
	
	/** The node selector which will be used to select which datasource node to call for each datasource operation. */
	private transient NodeSelector datasourceNodeSelector = new LRUNodeSelector();
	
	private transient DataSourceWrapperFactory datasourceWrapperFactory = null;
	
	public static final String DatasourceNodeSelectorHintName = "DatasourceNodeSelector";
	public static final String DataSourceNodeSelectorTieBreakerHintName = "DataSourceNodeSelectorTieBreaker";
	public static final String OperatorNodeSelectorHintName = "OperatorNodeSelector";
	public static final String OperatorNodeSelectorTieBreakerHintName = "OperatorNodeSelectorTieBreaker";
	public static final String OperatorNodeSelectorThresholdHintName = "OperatorNodeSelectorThreshold";
	public static final String MaxCollocationCostHintName = "MaxCollocationCost";
	public static final String ExcludeLocalHintName = "ExcludeLocal";
	public static final String NodeAssignmentPolicyHintName = "NodeAssignmentPolicy";
	public static final String ComplexPlanLevelsHintName = "ComplexPlanLevels";
	public static final String ComplexPlanNumNodesHintName = "ComplexPlanNumNodes";
	
	private static final Float OperatorNodeSelectorThresholdDef = 0.0f;
	private static final Float MaxCollocationCostDef = 30.0f;
	private static final Integer ComplexPlanLevelsDef = 3;
	private static final Integer ComplexPlanNumNodesDef = 20;
	private static final String OperatorNodeSelectorDef = RandomNodeSelector.class.getName();
	private static final String NodeAssignmentPolicyDef = SingleRemoteNodePolicy.class.getName();
	private static final String DataSourceNodeSelectorDef = LRUNodeSelector.class.getName();
	
	private static final boolean ExcludeLocalDef = true;
	
	private float operatorNodeSelectorThreshold = OperatorNodeSelectorThresholdDef;
	private float maxCollocationCost = MaxCollocationCostDef;
	private int complexPlanLevels = ComplexPlanLevelsDef;
	private int complexPlanNumNodes = ComplexPlanNumNodesDef;
	private boolean excludeLocal = ExcludeLocalDef;
	
	public WorkflowSearchAdaptor(EnvHintCollection hints) throws Exception 
	{
		this.Plan = new ExecutionPlan();
		this.Plan.EnvHints.AddHint(hints.GetHint("GCubeActionScope"));
		this.hints = hints;
		ParseHints();
		this.datasourceWrapperFactory = new DataSourceWrapperFactory();
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
		ResourceRegistry.startBridging();
	}
	
	public WorkflowSearchAdaptor(DataSourceWrapperFactoryConfig cfg, EnvHintCollection hints) throws Exception 
	{
		this.Plan = new ExecutionPlan();
		this.Plan.EnvHints.AddHint(hints.GetHint("GCubeActionScope"));
		this.hints = hints;
		ParseHints();
		this.datasourceWrapperFactory = new DataSourceWrapperFactory(cfg);
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
		ResourceRegistry.startBridging();
	}
	
	private void ParseHints() throws Exception
	{
		boolean thresholdOverriden = false;
		if(this.hints.HintExists(WorkflowSearchAdaptor.OperatorNodeSelectorThresholdHintName))
		{
			this.operatorNodeSelectorThreshold = Float.parseFloat(this.hints.GetHint(WorkflowSearchAdaptor.OperatorNodeSelectorThresholdHintName).Hint.Payload);
			logger.info("Using operator node selector threshold: " + this.operatorNodeSelectorThreshold);
			thresholdOverriden = true;
		}else logger.info("Using default operator node selector threshold: " + this.operatorNodeSelectorThreshold);
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.ComplexPlanLevelsHintName))
		{
			this.complexPlanLevels = Integer.parseInt(this.hints.GetHint(WorkflowSearchAdaptor.ComplexPlanLevelsHintName).Hint.Payload);
			logger.info("Using complex plan levels: " + this.complexPlanLevels);
		}else logger.info("Using default complex plan levels: " + this.complexPlanLevels);
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.ComplexPlanNumNodesHintName))
		{
			this.complexPlanNumNodes = Integer.parseInt(this.hints.GetHint(WorkflowSearchAdaptor.ComplexPlanNumNodesHintName).Hint.Payload);
			logger.info("Using complex plan node number: " + this.complexPlanNumNodes);
		}else logger.info("Using default complex plan node number: " + this.complexPlanNumNodes);
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.MaxCollocationCostHintName))
		{
			this.maxCollocationCost = Float.parseFloat(this.hints.GetHint(WorkflowSearchAdaptor.MaxCollocationCostHintName).Hint.Payload);
			logger.info("Using maximum collocation cost: " + this.maxCollocationCost);
		}else logger.info("Using default maximum collocation cost: " + this.maxCollocationCost);
		NodeSelector operatorNodeSelector = null;
		boolean opNodeSelectorOverriden = false;
		
		NodeSelector operatorNodeSelectorTieBreaker = null;
		if(this.hints.HintExists(WorkflowSearchAdaptor.OperatorNodeSelectorTieBreakerHintName))
		{
			String opNodeSelectorTieBreaker = this.hints.GetHint(WorkflowSearchAdaptor.OperatorNodeSelectorTieBreakerHintName).Hint.Payload;
			operatorNodeSelectorTieBreaker = (NodeSelector)Class.forName(opNodeSelectorTieBreaker).newInstance();
			logger.info("Using operator node selector tie breaker: " + opNodeSelectorTieBreaker);
		}
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.OperatorNodeSelectorHintName))
		{
			String opNodeSelector = this.hints.GetHint(WorkflowSearchAdaptor.OperatorNodeSelectorHintName).Hint.Payload;
			if(!opNodeSelector.equals(WorkflowSearchAdaptor.OperatorNodeSelectorDef))
				opNodeSelectorOverriden = true;
		
			if(operatorNodeSelectorTieBreaker != null)
			{
				Constructor c = Class.forName(opNodeSelector).getConstructor(NodeSelector.class);
				operatorNodeSelector = (NodeSelector)c.newInstance(operatorNodeSelectorTieBreaker);
			}
			else operatorNodeSelector = (NodeSelector)Class.forName(opNodeSelector).newInstance();
			logger.info("Using operator node selector: " + opNodeSelector);
		}
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.NodeAssignmentPolicyHintName))
		{
			Constructor c = null;
			if(!thresholdOverriden)
				c = Class.forName(this.hints.GetHint(WorkflowSearchAdaptor.NodeAssignmentPolicyHintName).Hint.Payload).getConstructor(NodeSelector.class);
			else
				c = Class.forName(this.hints.GetHint(WorkflowSearchAdaptor.NodeAssignmentPolicyHintName).Hint.Payload).getConstructor(NodeSelector.class, Float.class);
			if(!opNodeSelectorOverriden && operatorNodeSelectorTieBreaker == null) operatorNodeSelector = (NodeSelector)Class.forName(WorkflowSearchAdaptor.OperatorNodeSelectorDef).newInstance();
			if(!thresholdOverriden)
				this.nodeAssignmentPolicy = (NodeAssignmentPolicy)c.newInstance(operatorNodeSelector);
			else
				this.nodeAssignmentPolicy = (NodeAssignmentPolicy)c.newInstance(operatorNodeSelector, this.operatorNodeSelectorThreshold);
			logger.info("Using node assignment policy: " + this.hints.GetHint(WorkflowSearchAdaptor.NodeAssignmentPolicyHintName).Hint.Payload);
		}else
		{
			if(opNodeSelectorOverriden)
			{
				if(!thresholdOverriden)
				{
					Constructor c = Class.forName(this.NodeAssignmentPolicyDef).getConstructor(NodeSelector.class);
					this.nodeAssignmentPolicy = (NodeAssignmentPolicy)c.newInstance(operatorNodeSelector);
				}else
				{
					Constructor c = Class.forName(this.NodeAssignmentPolicyDef).getConstructor(NodeSelector.class, Float.class);
					this.nodeAssignmentPolicy = (NodeAssignmentPolicy)c.newInstance(operatorNodeSelector, this.operatorNodeSelectorThreshold);
				}
			}
		}
		
		NodeSelector datasourceNodeSelectorTieBreaker = null;
		if(this.hints.HintExists(WorkflowSearchAdaptor.DataSourceNodeSelectorTieBreakerHintName))
		{
			String dsNodeSelectorTieBreaker = this.hints.GetHint(WorkflowSearchAdaptor.DataSourceNodeSelectorTieBreakerHintName).Hint.Payload;
			datasourceNodeSelectorTieBreaker = (NodeSelector)Class.forName(dsNodeSelectorTieBreaker).newInstance();
			logger.info("Using data source node selector tie breaker: " + dsNodeSelectorTieBreaker);
		}
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.DatasourceNodeSelectorHintName))
		{
			String dsNodeSelector = this.hints.GetHint(WorkflowSearchAdaptor.DatasourceNodeSelectorHintName).Hint.Payload;
			if(!dsNodeSelector.equals(WorkflowSearchAdaptor.DataSourceNodeSelectorDef) || datasourceNodeSelectorTieBreaker != null)
			{
				if(datasourceNodeSelectorTieBreaker != null)
				{
					Constructor c = Class.forName(dsNodeSelector).getConstructor(NodeSelector.class);
					this.datasourceNodeSelector = (NodeSelector)c.newInstance(datasourceNodeSelectorTieBreaker);
				}
				else 
					this.datasourceNodeSelector = (NodeSelector)Class.forName(dsNodeSelector).newInstance();
				logger.info("Using data source node selector: " + dsNodeSelector);
			}
		}
		
		if(this.hints.HintExists(WorkflowSearchAdaptor.ExcludeLocalHintName))
			this.excludeLocal = Boolean.parseBoolean(this.hints.GetHint(WorkflowSearchAdaptor.ExcludeLocalHintName).Hint.Payload);
		
		logger.info((this.excludeLocal ? "E" : "Not e") + "xcluding local node");
		/*
		String scope = "/gcube/devsec";
		String providerInformationName = "gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";

		hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));
		InformationSystem.Init(providerInformationName, new EnvHintCollection());*/
		
	}
	
	public void SetInputPlan(PlanNode searchPlan) 
	{
		this.searchPlan = searchPlan;
	}
	
	public void SetExecutionId(String executionId)
	{
		this.executionId = executionId;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#CreatePlan()
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException,WorkflowInternalErrorException, WorkflowEnvironmentException
	{
		NodeExecutionInfo planInfo = null;
		try
		{
			List<ExecutionServer> allEs = ExecutionServer.getAll(true);
			Map<String, ExecutionServer> hnIdToExecutionServer = new HashMap<String, ExecutionServer>();
			//Map<String, gr.uoa.di.madgik.commons.infra.HostingNode> hnIdToHostingNode = new HashMap<String, gr.uoa.di.madgik.commons.infra.HostingNode>();
			RRExecutionServer2HnAdapter adapter = new RRExecutionServer2HnAdapter();
			
			List<ExecutionServer> executionServers = ExecutionServer.getAll(true);
			
			logger.info("Calling adaptall on CreatePlan on " + executionServers.size() + " execution nodes");
			List<gr.uoa.di.madgik.commons.infra.HostingNode> hns = adapter.adaptAll(executionServers);
			
			logger.debug("found execution nodes to execute the complex plan");
			logger.debug("---------------------------------------------------");
			for(gr.uoa.di.madgik.commons.infra.HostingNode hn : hns) {
				logger.debug(hn.toXML());
			}
			logger.debug("---------------------------------------------------");
			
			if(hns.size() == 0) throw new WorkflowInternalErrorException("No execution nodes were found in the infrastructure");
			boolean localFound = false;
			int localIndex = 0;
			for(gr.uoa.di.madgik.commons.infra.HostingNode hn : hns)
			{
				if(hn.isLocal())
				{
					localFound = true;
					break;
				}
				localIndex++;
			}
			//for(gr.uoa.di.madgik.commons.infra.HostingNode hn : hns) hnIdToHostingNode.put(hn.getId(), hn);
			SearchPlanAnalyzer analyzer = new SearchPlanAnalyzer(this.complexPlanLevels, this.complexPlanNumNodes);
			boolean isComplex = analyzer.isComplex(this.searchPlan);
			if(localFound && (excludeLocal || isComplex))
			{
				logger.info("excludeLocal is " + excludeLocal + ", isComplex is " + isComplex + ". Removing local from candidates");
				hns.remove(localIndex);
			}
			for(ExecutionServer es : allEs) hnIdToExecutionServer.put(es.getHostingNode().getID(), es);
			if(this.searchPlan==null) throw new WorkflowValidationException("No search plan specified");
			
			float recomputedCollocationCost = maxCollocationCost;
			float cost = analyzer.calculateOperatorCost(this.searchPlan);
			float utilizationFactor = (cost/maxCollocationCost)/hns.size();
			if(utilizationFactor > 1.0f) recomputedCollocationCost = maxCollocationCost*utilizationFactor;
			
			SearchPlanRewriter rewriter = new SearchPlanRewriter(recomputedCollocationCost);
			this.searchPlan = rewriter.rewrite(this.searchPlan);
			//cost = analyzer.calculateOperatorCost(this.searchPlan);
			//commitFactor = (cost/recomputedThreshold)/hns.size();
			NodeAssignmentPolicy policy = this.nodeAssignmentPolicy;
			if(!isComplex) 
			{
				if(localFound && !excludeLocal)
				{
					policy = new LocalOnlyPolicy();
					logger.info("Non-complex plan, executing locally");
				}else
				{
					policy = new SingleRemoteNodePolicy(new RandomNodeSelector());
					policy.reset();
					if(excludeLocal) logger.info("Non-complex plan, local node excluded. Executing in single remote node");
					else if(!localFound) logger.info("Non-complex plan but could not determine which node is local (or local node is not registered as execution node). Executing in single remote node");
				}
			}else
				logger.info("Complex plan, policy: " + this.nodeAssignmentPolicy.getClass().getName() + (excludeLocal ? ", local node excluded" : ""));
			NodeAssignmentTree nst = new NodeAssignmentTree(hns, policy, datasourceNodeSelector, maxCollocationCost);
			NodeAssignmentNode nodeAssignmentPlan = nst.build(this.searchPlan);
			Map<String, Float> utilizationFactors = nst.getUtilizationFactors();
			planInfo = this.ConstructWorkflow(nodeAssignmentPlan, hnIdToExecutionServer, utilizationFactors);
			if(planInfo.wrapperNode.wrapper instanceof ProcessingWrapper) 
			{
				((ProcessingWrapper)planInfo.wrapperNode.wrapper).elevate();
				((SequencePlanElement)planInfo.element).ElementCollection.add(((ProcessingWrapper)planInfo.wrapperNode.wrapper).getElevationElement());
				this.Plan.Variables.Add(((ProcessingWrapper)planInfo.wrapperNode.wrapper).getElevationVariable());
			}
			
			if(nodeAssignmentPlan instanceof OperatorNodeAssignmentNode)
			{
				OperatorNodeAssignmentNode opNode = (OperatorNodeAssignmentNode)nodeAssignmentPlan;
				if(!opNode.element.assignedNode.isLocal())
				{	
					logger.info("Boundary cross from local to " + opNode.element.assignedNode.getId() + "(" + 
				                opNode.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty) + ")");
					BoundaryPlanElement bpe = new BoundaryPlanElement();
					BoundaryConfig bcfg = new BoundaryConfig();
					bcfg.HostName = opNode.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty);
					bcfg.Port = Integer.parseInt(hnIdToExecutionServer.get(opNode.element.assignedNode.getId()).getPort());
					bcfg.NozzleConfig = new TCPServerNozzleConfig(false, 0);
					
					bpe.SetName(bcfg.HostName);
					bpe.Config = bcfg;
					bpe.Isolation=new BoundaryIsolationInfo();
					bpe.Isolation.CleanUp=true;
					bpe.Isolation.Isolate=false;
					bpe.Root = planInfo.element;
					planInfo.element = bpe;
				}
			}
				
		}catch(Exception e)
		{
			throw new WorkflowInternalErrorException("Could not construct workflow", e);
		}
		this.Plan.Root = planInfo.element;
		this.wrapperTree = planInfo.wrapperNode;	
		

		logger.trace("****************************************");
		try {
			logger.trace(this.Plan.Serialize());
		} catch (ExecutionSerializationException e) {
			logger.trace("error while serializing plan.", e);
		}
		logger.trace("****************************************");
		
	}
	
	public void setWrapperTree(WrapperNode wrapperNode){
		this.wrapperTree = wrapperNode;
	}
	
	public WrapperNode getWrapperTree(){
		return this.wrapperTree;
	}
	
	public static String serializePlan(Serializable obj) throws IOException{
		String serializedPlan = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.flush();
		serializedPlan = Base64.encodeBase64String(baos.toByteArray());
		
		try {
			baos.close();
		} catch (Exception e) {
		}
		try {
			oos.close();
		} catch (Exception e) {
		}
		return serializedPlan;
//		return serializedPlan;
	}
	
	public static Object deserializePlan(String serializedPlan) throws IOException, ClassNotFoundException{
		
		byte[] data= Base64.decodeBase64(serializedPlan);
//		byte[] data= serializedPlan.getBytes();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		
		try {
			ois.close();
		} catch (Exception e) {
		}
		
		return o;
	}
	
	public VariableCollection getVariableCollection(){
		return this.Plan.Variables;
	}
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetCreatedPlan()
	 */
	public ExecutionPlan GetCreatedPlan()
	{
		return this.Plan;
	}
	
	public void setCreatedPlan(ExecutionPlan plan)
	{
		this.Plan = plan;
	}
	
	public String GetExecutionID()
	{
		return this.executionId;
	}
	
	public OutputVariableNode getOutputVariables() {
		if(this.ioVariableNodes != null)
			return this.ioVariableNodes;
		
		return ConstructOutputVariableTree(this.wrapperTree);
	}
	
	public String ExecutePlan() throws WorkflowValidationException, ExecutionException 
	{
		logger.trace("******************");
		logger.trace(this.Plan.Serialize());
		logger.trace("******************");
		
		if(this.Plan.Root == null)
			throw new WorkflowValidationException("No execution plan has been created");
		
		logger.trace("Submitting plan...");
		this.Handle = ExecutionEngine.Submit(this.Plan);
		logger.trace("Submitting plan...OK");
		
		Object synchCompletion=new Object();
		this.executionId=ExecutionDirectory.ReserveKey();
		
		logger.trace("Registering observer...");
		ExecutionObserver obs=new ExecutionObserver(this.executionId, DirectoryEntryType.Generic, -1l, this.Handle, this, false, synchCompletion);
		ExecutionDirectory.Register(obs);
		this.Handle.RegisterObserver(obs);
		logger.trace("Registering observer...");
		
		
		logger.trace("Executing plan " + this.executionId);
		ExecutionEngine.Execute(this.Handle);
		logger.trace("Before synch");
		synchronized (synchCompletion)
		{
			while(!obs.IsCompleted())
			{
				try
				{
					synchCompletion.wait();
				}catch(Exception ex){}
			}
		}
		logger.trace("After synch");
		logger.trace("Executing plan " + this.executionId+ " OK");
		
		if(!this.Handle.IsCompleted()) logger.warn("Not completed! Why am I here?");
		else if(this.Handle.IsCompletedWithSuccess()) logger.info("Plan successfully completed");
		else if(this.Handle.IsCompletedWithError()) 
		{
			String errorString="Plan unsuccessfully completed with error";
			if(this.Handle.GetCompletionError() instanceof ExecutionRunTimeException) errorString+=" of cause "+((ExecutionRunTimeException)this.Handle.GetCompletionError()).GetCauseFullName();
			logger.info(errorString,this.Handle.GetCompletionError());
		}
		else  logger.warn("Completed but neither with success or failure!");

		logger.info("Returning grs2 locator: " + this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue());
		
		return this.wrapperTree.wrapper.getOutputVariable().Value.GetStringValue();
	}
	
	public ExecutionException GetCompletionError() throws WorkflowValidationException {
		if(this.Plan == null)
			throw new WorkflowValidationException("No execution plan has been created");
		return this.Handle.GetCompletionError();
	}
	
	private NodeExecutionInfo ConstructWorkflow(NodeAssignmentNode planNode, Map<String, ExecutionServer> hnIdToExecutionServer, Map<String, Float> commitFactors) throws Exception 
	{
		if(planNode instanceof OperatorNodeAssignmentNode) 
		{
			OperatorNodeAssignmentNode operatorNode = (OperatorNodeAssignmentNode)planNode;
			List<IPlanElement> childPlanElements = new ArrayList<IPlanElement>();
			List<WrapperNode> childWrappers = new ArrayList<WrapperNode>();
			for(NodeAssignmentNode child : operatorNode.getChildren()) {
				NodeExecutionInfo childExecutionInfo = ConstructWorkflow(child, hnIdToExecutionServer, commitFactors);
				if(child instanceof OperatorNodeAssignmentNode)
				{
					OperatorNodeAssignmentNode opChild = (OperatorNodeAssignmentNode)child;
					if(!planNode.element.assignedNode.getId().equals(opChild.element.assignedNode.getId()))
					{
						//boundary cross
						logger.info("Boundary cross from " + planNode.element.assignedNode.getId() + "(" + planNode.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty) +
									") to" + opChild.element.assignedNode.getId() + "(" + opChild.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty) + ")");
						((ProcessingWrapper)childExecutionInfo.wrapperNode.wrapper).elevate();
						((SequencePlanElement)childExecutionInfo.element).ElementCollection.add(((ProcessingWrapper)childExecutionInfo.wrapperNode.wrapper).getElevationElement());
						this.Plan.Variables.Add(((ProcessingWrapper)childExecutionInfo.wrapperNode.wrapper).getElevationVariable());
						
						BoundaryPlanElement bpe = new BoundaryPlanElement();
						BoundaryConfig bcfg = new BoundaryConfig();
						bcfg.HostName = opChild.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty);
						bcfg.Port = Integer.parseInt(hnIdToExecutionServer.get(opChild.element.assignedNode.getId()).getPort());
						bcfg.NozzleConfig = new TCPServerNozzleConfig(false, 0);
						
						bpe.SetName(bcfg.HostName);
						bpe.Config = bcfg;
						bpe.Isolation=new BoundaryIsolationInfo();
						bpe.Isolation.CleanUp=true;
						bpe.Isolation.Isolate=false;
						bpe.Root = childExecutionInfo.element;
						childExecutionInfo.element = bpe;
					}
				}
				childPlanElements.add(childExecutionInfo.element);
				childWrappers.add(childExecutionInfo.wrapperNode);
			}
			
			FlowPlanElement flow = new FlowPlanElement();
			flow.ElementCollection = childPlanElements;
			
			NodeExecutionInfo execInfo = ConstructProcessingPlanElement(((OperatorNodeAssignmentNode)planNode), childWrappers, commitFactors);
			SequencePlanElement seq = new SequencePlanElement();
			seq.ElementCollection.add(flow);
			seq.ElementCollection.add(execInfo.element);
			return new NodeExecutionInfo(seq, execInfo.wrapperNode);
		}else if(planNode instanceof DataSourceNodeAssignmentNode) {
			logger.info("In WorkflowSearchAdaptor planNode : " + ((DataSourceNodeAssignmentNode)planNode).toXML());
			
			return ConstructDataSourcePlanElement((DataSourceNodeAssignmentNode)planNode);
			
		}else
			throw new Exception("Unrecognized search plan node type");
	}
	
	private NodeExecutionInfo ConstructProcessingPlanElement(OperatorNodeAssignmentNode planNode, List<WrapperNode> childWrappers, Map<String, Float> commitFactors) throws Exception 
	{
		
		List<NamedDataType> inputVariables = new ArrayList<NamedDataType>();
		for(WrapperNode childWrapper : childWrappers)
			inputVariables.add(childWrapper.wrapper.getOutputVariable());
			
		String functionality = ((OperatorNode)planNode.element.processingNode).getFunctionality();
		
		ProcessingElementConstructor elementConstructor = null;
		IPlanElement planElement = null;
		FunctionalityWrapper wrapper = null;
		
		if(functionality.equalsIgnoreCase(Constants.MERGE))
			elementConstructor = new MergeElementConstructor();
		else if(functionality.equalsIgnoreCase(Constants.JOIN)) 
			elementConstructor = new JoinElementConstructor();
		else if(functionality.equalsIgnoreCase(Constants.EXCEPT))
			elementConstructor = new ExceptElementConstructor();
		else if(functionality.equalsIgnoreCase(Constants.FUSE))
			elementConstructor = new MergeElementConstructor();
			
		Integer bufferCapacity = null;
		float commitFactor = commitFactors.get(planNode.element.assignedNode.getId());
		if(commitFactor > 1.0f)
		{
			bufferCapacity = limitBufferCapacity(commitFactor);
			logger.info("Node " + planNode.element.assignedNode.getId() + "(" + planNode.element.assignedNode.getPropertyByName(HostingNode.HostnameProperty) + ")" +
					" is overcommitted (factor:" + commitFactor + "). Limiting buffer to: " + bufferCapacity);
		}
		
		logger.error("planNode : " + planNode);
		logger.error("planNode.element : " + planNode.element);
		logger.error("planNode.element.processingNode : " + planNode.element.processingNode);
		
		logger.error("elementConstructor : " + elementConstructor);
		
		NodeExecutionInfo tmp = elementConstructor.constructPlanElement(planNode.element.processingNode.getFunctionalArgs(), inputVariables, bufferCapacity);
		wrapper = tmp.wrapperNode.wrapper;
		wrapper.addVariablesToPlan(this.Plan);
		planElement = tmp.element;
		
		return new NodeExecutionInfo(planElement, new WrapperNode(wrapper, childWrappers));
	}
	
	private NodeExecutionInfo ConstructDataSourcePlanElement(DataSourceNodeAssignmentNode planNode) throws WorkflowValidationException, ExecutionValidationException, ExecutionSerializationException, ResourceRegistryException, Exception 
	{
		
		String selectedId = planNode.instanceId;

		IRRElement datasource = QueryHelper.GetSourceServiceById(selectedId);
		if(datasource == null) throw new Exception("Datasource service with id " + selectedId + " does not exist");
		
		String serviceEndpoint;
		String resourceKey;
		if(datasource instanceof DataSourceService) {
			serviceEndpoint = ((DataSourceService)datasource).getEndpoint();
			resourceKey = ((DataSourceService)datasource).getID();
			
			logger.info("serviceEndpoint : " + serviceEndpoint);
			logger.info("resourceKey     : " + resourceKey);
			
			
//			if(((DataSourceService)datasource).getScopes() != null && !((DataSourceService)datasource).getScopes().isEmpty())
//			{
//				if(!((DataSourceService)datasource).getScopes().contains(this.scope))
//					throw new WorkflowValidationException("Could not find a source running on given scope: " + this.scope);
//			}
		}else
			throw new Exception("Unrecognized datasource type retrieved from registry: + " + datasource.getClass().getName());
	
		logger.info("in WorkflowSearchAdaptor:ConstructDataSourcePlanElement serviceEndpoint : " + serviceEndpoint);
		
	//	String serviceEndpoint = "dummyEndPoint";//TODO remove
	//	String resourceKey = "123-890"; //TODO remove
		DataSourceWrapper wrapper = null;
		if(datasource instanceof FTIndexService)
			wrapper = datasourceWrapperFactory.newFullTextIndexNodeWrapper(serviceEndpoint, this.hints);
		else if(datasource instanceof OpenSearchDataSourceService)
			wrapper = datasourceWrapperFactory.newOpenSearchDataSourceServiceWrapper(serviceEndpoint, this.hints);
		else if (datasource instanceof SruConsumerService)
			wrapper = datasourceWrapperFactory.newSruConsumerServiceWrapper(serviceEndpoint, this.hints);
		else
			throw new Exception("Unrecognized datasource type retrieved from registry: + " + datasource.getClass().getName());
		wrapper.setQuery(((DataSourceNode)planNode.element.processingNode).getCqlInput());
		wrapper.setResourceKey(resourceKey);
		IPlanElement datasourceElement = wrapper.constructPlanElements()[0];
		wrapper.addVariablesToPlan(this.Plan);
		return new NodeExecutionInfo(datasourceElement, new WrapperNode(wrapper, null));
	}
	
	private int limitBufferCapacity(float commitFactor)
	{
		if(commitFactor <= 1.0f) return RecordWriter.DefaultBufferCapacity;
		int c = (int)Math.floor(RecordWriter.DefaultBufferCapacity * Math.exp(-commitFactor+1.0f));
		if(c < 10) return 10;
		return c;
		
	}
	
	private OutputVariableNode ConstructOutputVariableTree(WrapperNode node) 
	{
		if(node.children == null)
			return new OutputVariableNode(node.wrapper.getOutputVariable().Name, null);
		List<OutputVariableNode> children = new ArrayList<OutputVariableNode>();
		for(WrapperNode wrapperChild : node.children)
			children.add(ConstructOutputVariableTree(wrapperChild));
		return new OutputVariableNode(node.wrapper.getOutputVariable().Name, children);	
	}

	@Override
	public void SetAdaptorResources(IAdaptorResources Resources)
			throws WorkflowValidationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<IOutputResource> GetOutput() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	

}
