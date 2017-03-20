package gr.uoa.di.madgik.workflow.adaptor;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.PlanConfig.ConnectionMode;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.BreakPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ConditionalPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.StoreMode;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.LoopPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ParameterProcessingPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WaitPlanElement;
import gr.uoa.di.madgik.execution.plan.element.attachment.ExecutionAttachment;
import gr.uoa.di.madgik.execution.plan.element.attachment.ExecutionAttachment.AttachmentLocation;
import gr.uoa.di.madgik.execution.plan.element.condition.BooleanVariableCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeLeaf;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode.NodeVerb;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode.PostVerb;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionalFlow;
import gr.uoa.di.madgik.execution.plan.element.condition.TimeOutPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionRetry;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterExternalFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping.MapType;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.plan.trycatchfinally.CatchElement;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AdaptorGridResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.JobLogInfoFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.JobStatusBreakLoopFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.JobStatusExternalFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.JobStatusSuccessFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.OutputSandboxGridResource;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The Class WorkflowGridAdaptor constructs an {@link ExecutionPlan} that can mediate to submit a job
 * described through a JDL file using a Grid UI node. After its submission the job is monitored for its 
 * status and once completed the output files are retrieved and stored in the Storage System.
 * </p>
 * <p>
 * The resources that are provided and need to be moved to the Grid UI are all transfered through the 
 * Storage System. They are stored once the plan is constructed and are then retrieved once the execution 
 * is started. This does not include the provided user proxy which is transfered as an attachment directly to
 * the remote node to allow secure transfer if the SSL communication option is enabled.
 * </p> 
 * <p>
 * The entire execution process takes place in the Grid UI node. This node is picked from the Information System
 * and is currently chosen randomly from all the available ones. Currently once the node has been picked, the 
 * execution cannot be moved to a different one even if there is a problem communicating with that node. The execution 
 * that takes place is a sequential series of steps. These steps include
 *  - Contact the remote node
 *  - Retrieval of the data stored in the Storage System and these include the resources marked as one of
 *  	{@link AttachedGridResource.ResourceType#Config}, {@link AttachedGridResource.ResourceType#InData},
 *  	{@link AttachedGridResource.ResourceType#JDL}
 *  - Submit the job using the provided JDL file and optionally any configuration additionally provided 
 *  	using the provided user proxy certificate
 *  - Go into a loop until either the job is completed or a timeout has expired (If a timeout has been set)
 *  	- Wait for a defined period
 *  	- Retrieve the job status
 *  	- Retrieve the job logging info
 *  	- Process the results of the above two steps
 *  - Check the reason the loop ended
 *  	- If a timeout happened, cancel the job
 *  	- If the job terminated successfully retrieve the output files of the job
 * </p>
 * 
 * TODO:
 *  - Handle errors and not let every exception stop the execution.
 *  - Allow for relocation of execution
 *  - If relocated cancel previous execution
 *  - Add SSL option in communication
 *  - Allow multiple JDLs and collection style submission
 *  - Delete files stored in Storage System if error in plan construction and after completion
 * 
 * @author gpapanikos
 */
public class WorkflowGridAdaptor implements IWorkflowAdaptor
{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkflowGridAdaptor.class);
	
	/** The Resources needed to construct and execute the plan. */
	private AdaptorGridResources Resources=null;
	
	/** The Output resources that will contain info on the produced output. */
	private Set<IOutputResource> OutputResources=null;
	
	/** The constructed execution Plan. */
	private ExecutionPlan Plan=null;
	
	/** The execution id associated with the created Plan. Can be set optionally. */
	private String ExecutionId=null;
	
	private EnvHintCollection Hints=new EnvHintCollection();
	
	/** Information on the Grid UI node that will manage the job submission. */
	private NodeInfo GridUINode=null;
	
	private static long DefaultTimeout=Long.MAX_VALUE;
	
	private static long DefaultWaitPeriod=60*1000;
	
	private static long DefaultRetryOnErrorPeriod=1000*60;
	
	private static int DefaultRetryOnErrorTimes=10;
	
	/**
	 *  Timeout period to wait for the job to be completed. A {@link Long#MIN_VALUE} or
	 *  {@link Long#MAX_VALUE} or a zero value indicates that no timeout should be used. 
	 */
	public long Timeout=WorkflowGridAdaptor.DefaultTimeout;
	
	/** 
	 * The period every which the job status and logging info should be polled.
	 */
	public long WaitPeriod=WorkflowGridAdaptor.DefaultWaitPeriod;
	
	public long RetryOnErrorPeriod=WorkflowGridAdaptor.DefaultRetryOnErrorPeriod;
	
	public int RetryOnErrorTimes=WorkflowGridAdaptor.DefaultRetryOnErrorTimes;

	/**
	 * Instantiates a new workflow grid JDL adaptor.
	 */
	public WorkflowGridAdaptor()
	{
		this.OutputResources=new HashSet<IOutputResource>();
	}
	
	/**
	 * Sets the resources that the adaptor needs to construct the plan. These must be of type
	 * {@link AdaptorGridResources}.
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#SetAdaptorResources(gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources)
	 */
	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException
	{
		if(!(Resources instanceof AdaptorGridResources)) throw new WorkflowValidationException("Invalid adaptor resources provided");
		this.Resources=(AdaptorGridResources)Resources;
		this.Resources.Validate();
	}
	
	public void SetExecutionId(String executionId)
	{
		this.ExecutionId = executionId;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#CreatePlan()
	 */
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException,WorkflowInternalErrorException, WorkflowEnvironmentException
	{
		if(this.Resources==null) throw new WorkflowValidationException("No resources specified");
		if(this.Timeout<=0) this.Timeout=WorkflowGridAdaptor.DefaultTimeout;
		if(this.WaitPeriod<=0) this.WaitPeriod=WorkflowGridAdaptor.DefaultWaitPeriod;
		this.CreateEnvironmentHints();
		this.ConstructWorkflow();
		this.ExcludeOutputResourcesCleanUp();
	}
	
	private void ExcludeOutputResourcesCleanUp()
	{
		for(IOutputResource res : this.OutputResources)
		{
			if(!(res instanceof OutputSandboxGridResource)) continue;
			this.Plan.CleanUpSSExclude.Add(((OutputSandboxGridResource)res).VariableID);
		}
	}
	
	private void CreateEnvironmentHints()
	{
		AttachedGridResource res= this.Resources.GetScopeResource();
		if(res!=null)
		{
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(res.Value)));
		}
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetCreatedPlan()
	 */
	public ExecutionPlan GetCreatedPlan()
	{
		return this.Plan;
	}

	/**
	 * Retrieves the set of output resources containing info on the products of the workflow after the plan
	 * is executed. the resources are of type {@link OutputSandboxGridResource}
	 * 
	 * @see gr.uoa.di.madgik.workflow.adaptor.IWorkflowAdaptor#GetOutput()
	 */
	public Set<IOutputResource> GetOutput()
	{
		return this.OutputResources;
	}
	
	/**
	 * Construct the workflow to be executed
	 * 
	 * @throws WorkflowInternalErrorException An internal error occurred
	 * @throws WorkflowValidationException An validation error occurred
	 * @throws WorkflowEnvironmentException An error from the environment occurred
	 */
	private void ConstructWorkflow() throws WorkflowInternalErrorException,WorkflowValidationException, WorkflowEnvironmentException
	{
		try
		{
			// Store the resources that will be need to be moved to the Grid UI except the user proxy 
			try
			{
				Set<AttachedGridResource.ResourceType> ResourcesToStore=new HashSet<AttachedGridResource.ResourceType>();
				ResourcesToStore.add(AttachedGridResource.ResourceType.Config);
				ResourcesToStore.add(AttachedGridResource.ResourceType.InData);
				ResourcesToStore.add(AttachedGridResource.ResourceType.JDL);
				this.Resources.StoreResources(ResourcesToStore,this.Hints);
			}catch(Exception ex)
			{
				throw new WorkflowEnvironmentException("Could not store resources in storage system", ex);
			}
			this.Plan=new ExecutionPlan();
			this.Plan.Config=new PlanConfig();
			this.Plan.Config.ConnectionCallbackTimeout=1000*60*60*24; //24 hours
			this.Plan.Config.ModeOfConnection=ConnectionMode.Callback;
			this.Plan.EnvHints=this.Hints;
			//All the execution will take place in the remote Grid UI node
			BoundaryPlanElement  bound=this.ConstructBoundaryElement();
			this.Plan.Root=bound;
			
//			if(!this.GridUINode.TypeSpecificExtensions.containsKey(ExtensionPair.GLITE_LOCATION)) throw new WorkflowEnvironmentException("Selected Grid UI node does not expose the needed environment extensions");
//			if(!this.GridUINode.TypeSpecificExtensions.containsKey(ExtensionPair.ORIGINAL_GLOBUS_LOCATION)) throw new WorkflowEnvironmentException("Selected Grid UI node does not expose the needed environment extensions");

			//All of the execution in the remote host will take place in a sequential order
			bound.Root=new SequencePlanElement();
			//Retrieve the data stored in the Storage System
			if(this.Resources.GetConfigResource()!=null)
			{
				((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(this.Resources.GetConfigResource()));
			}
			((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(this.Resources.GetJDLResource()));
			for(AttachedGridResource att : this.Resources.GetInDataResources())
			{
				((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(att));
			}
			String jobIDFile=UUID.randomUUID().toString()+".id.file.txt";
			SimpleInOutParameter LoopBreakCondition=AdaptorUtils.GetInOutParameter(true,this.Plan);
			SimpleInOutParameter StatusSuccessCondition=AdaptorUtils.GetInOutParameter(false,this.Plan);
			//Submit the job
			((SequencePlanElement)bound.Root).ElementCollection.add(this.ConstructSubmitJobElement(jobIDFile));
			//Store the file containing the id of the submitted job
			((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateStoreFileElement(jobIDFile));
			//Go into a loop checking the status of the job
			((SequencePlanElement)bound.Root).ElementCollection.add(this.CheckLoop(jobIDFile,LoopBreakCondition,StatusSuccessCondition));
			//Check if the loop broke because of a timeout
			ConditionalPlanElement cond=this.CheckTimeoutAndRetrieveOutput(LoopBreakCondition,StatusSuccessCondition,jobIDFile);
			if(cond!=null)((SequencePlanElement)bound.Root).ElementCollection.add(cond);
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Creates a condition element that will check if the loop of checking the status of the job
	 * has been terminated because the job has been successfully terminated or because a timeout has occurred.
	 * If a timeout occurred, a job cancellation is performed 
	 * 
	 * @param LoopBreakCondition the loop break condition which if true means that the job has finished and no timeout occurred
	 * @param jobIDFile The id of the submitted job
	 * 
	 * @return the conditional plan element or null if no timeout has been specified 
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ConditionalPlanElement CheckTimeoutAndRetrieveOutput(SimpleInOutParameter LoopBreakCondition,SimpleInOutParameter StatusSuccessCondition,String jobIDFile) throws WorkflowValidationException
	{
		ConditionalPlanElement cond=new ConditionalPlanElement();
		if(this.Timeout==Long.MIN_VALUE || this.Timeout==Long.MAX_VALUE || this.Timeout==0)
		{
			ConditionalFlow retrieveOutput=this.GetOutputConditionalFlow(StatusSuccessCondition, jobIDFile);
			if(retrieveOutput!=null) cond.IfFlow=retrieveOutput;
			cond.ElseIfFlows.clear();
			cond.ElseFlow=null;
		}
		else
		{
			cond.IfFlow=this.GetTimeoutConditionalFlow(LoopBreakCondition, jobIDFile);
			cond.ElseIfFlows.clear();
			ConditionalFlow retrieveOutput=this.GetOutputConditionalFlow(StatusSuccessCondition, jobIDFile);
			if(retrieveOutput!=null) cond.ElseIfFlows.add(retrieveOutput);
			cond.ElseFlow=null;
		}
		return cond;
	}
	
	private ConditionalFlow GetOutputConditionalFlow(SimpleInOutParameter StatusSuccessCondition,String jobIDFile) throws WorkflowValidationException
	{
		if(this.Resources.GetOutDataResources().size()<=0) return null;
		ConditionalFlow cond= new ConditionalFlow();
		cond.Condition=new ConditionTree();
		cond.Condition.Root=new ConditionTreeLeaf();
		((ConditionTreeLeaf)cond.Condition.Root).Condition=new BooleanVariableCondition();
		((BooleanVariableCondition)((ConditionTreeLeaf)cond.Condition.Root).Condition).FlagParameter=StatusSuccessCondition;
		cond.Root=new SequencePlanElement();

		String OutputDirectory=UUID.randomUUID().toString()+".output.dir";
		((SequencePlanElement)cond.Root).ElementCollection.add(this.RetrieveOutput(jobIDFile,OutputDirectory));
		
		String compressName = "Output";
		((SequencePlanElement)cond.Root).ElementCollection.add(this.CompressOutput(compressName, OutputDirectory));
		
		AttachedGridResource att = new AttachedGridResource(compressName+".tar", null);
		((SequencePlanElement)cond.Root).ElementCollection.add(this.ChockError(this.CreateStoreFileElement(att, OutputDirectory)));
		
		return cond;
	}
	
	private TryCatchFinallyPlanElement ChockError(IPlanElement element)
	{
		TryCatchFinallyPlanElement tcf=new TryCatchFinallyPlanElement();
		tcf.TryFlow=element;
		CatchElement c=new CatchElement();
		c.Error=null;
		c.Rethrow=false;
		c.Root=null;
		tcf.CatchFlows.add(c);
		tcf.FinallyFlow=null;
		return tcf;
	}
	
	private ConditionalFlow GetTimeoutConditionalFlow(SimpleInOutParameter LoopBreakCondition,String jobIDFile) throws WorkflowValidationException
	{
		ConditionalFlow cond= new ConditionalFlow();
		cond.Condition=new ConditionTree();
		cond.Condition.Root=new ConditionTreeNode();
		((ConditionTreeNode)cond.Condition.Root).Verb=NodeVerb.AND;
		((ConditionTreeNode)cond.Condition.Root).Post=PostVerb.Negate;
		ConditionTreeLeaf boolCond=new ConditionTreeLeaf();
		boolCond.Condition=new BooleanVariableCondition();
		((BooleanVariableCondition)boolCond.Condition).FlagParameter=LoopBreakCondition;
		((ConditionTreeNode)cond.Condition.Root).Childen.add(boolCond);
		cond.Root=new SequencePlanElement();
		((SequencePlanElement)cond.Root).ElementCollection.add(this.CancelJob(jobIDFile));
		BreakPlanElement br= new BreakPlanElement();
		br.Message="Timeout while waiting for job to complete";
		((SequencePlanElement)cond.Root).ElementCollection.add(br);
		return cond;
	}
	
	/**
	 * Creates a loop element with a body that sequentially pauses for some period, then checks the status 
	 * of the job and then retrieves logging information of the job. Subsequently the outputs of the last two 
	 * steps are processed to retrieve the status of the job and also to emit the logging info back to the
	 * caller. The loop is continued until either the job status indicates that the operation has been completed
	 * or because a timeout occurred. 
	 * 
	 * @param jobIDFile the job id file
	 * @param LoopBreakCondition the parameter holding the info on whether the job terminated
	 * 
	 * @return the loop plan element
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private LoopPlanElement CheckLoop(String jobIDFile,SimpleInOutParameter LoopBreakCondition,SimpleInOutParameter StatusSuccessCondition) throws WorkflowValidationException
	{
		LoopPlanElement loop=new LoopPlanElement();
		loop.LoopCondition=this.GetCheckLoopCondition(LoopBreakCondition);
		loop.Root=new SequencePlanElement();
		((SequencePlanElement)loop.Root).ElementCollection.add(this.PauseToRecheck());
		String StatusOutputVariableID=AdaptorUtils.GetInOutPrameter(this.Plan).VariableName;
		ShellPlanElement sts=this.CheckStatus(jobIDFile,StatusOutputVariableID);
		((SequencePlanElement)loop.Root).ElementCollection.add(sts);
		String LoggingOutputVariableID=AdaptorUtils.GetInOutPrameter(this.Plan).VariableName;
		ShellPlanElement lognfo=this.CheckLoggingInfo(jobIDFile,LoggingOutputVariableID);
		((SequencePlanElement)loop.Root).ElementCollection.add(lognfo);
		ParameterProcessingPlanElement proc=this.ProcessOutputs(StatusOutputVariableID, LoggingOutputVariableID,LoopBreakCondition.VariableName,lognfo.GetID(),StatusSuccessCondition.VariableName);
		((SequencePlanElement)loop.Root).ElementCollection.add(proc);
		return loop;
	}
	
	/**
	 * Constructs the loop condition that will check if the job is still running or if a timeout has occurred
	 * and is used by {@link WorkflowGridAdaptor#CheckLoop(String, SimpleInOutParameter)}
	 * 
	 * @param LoopBreakCondition the loop break condition
	 * 
	 * @return the condition tree
	 */
	private ConditionTree GetCheckLoopCondition(SimpleInOutParameter LoopBreakCondition)
	{
		ConditionTree condt=new ConditionTree();
		if(this.Timeout==Long.MIN_VALUE || this.Timeout==Long.MAX_VALUE || this.Timeout==0)
		{
			condt.Root=new ConditionTreeLeaf();
			((ConditionTreeLeaf)condt.Root).Condition=new BooleanVariableCondition();
			((BooleanVariableCondition)((ConditionTreeLeaf)condt.Root).Condition).FlagParameter=LoopBreakCondition;
		}
		else
		{
			condt.Root=new ConditionTreeNode();
			((ConditionTreeNode)condt.Root).Verb=NodeVerb.AND;
			
			ConditionTreeLeaf jobDone=new ConditionTreeLeaf();
			jobDone.Condition=new BooleanVariableCondition();
			((BooleanVariableCondition)jobDone.Condition).FlagParameter=LoopBreakCondition;
			
			ConditionTreeLeaf jobTimedOut=new ConditionTreeLeaf();
			jobTimedOut.Condition=new TimeOutPlanCondition();
			((TimeOutPlanCondition)jobTimedOut.Condition).TimeoutThreshold=this.Timeout;
			
			((ConditionTreeNode)condt.Root).Childen.add(jobDone);
			((ConditionTreeNode)condt.Root).Childen.add(jobTimedOut);
		}
		return condt;
	}
	
	/**
	 * Constructs the wait element that will pause the execution once every loop entrance
	 * and is used by {@link WorkflowGridAdaptor#CheckLoop(String, SimpleInOutParameter)}
	 * 
	 * @return the wait plan element
	 */
	private WaitPlanElement PauseToRecheck()
	{
		WaitPlanElement wait=new WaitPlanElement();
		wait.WaitPeriod=this.WaitPeriod;
		return wait;
	}
	
	/**
	 * Constructs a processing element that will receive as input the outputs of the job status and job logging
	 * info and will parse it to update the job status and to emit progress events on the execution of the job
	 * and is used by {@link WorkflowGridAdaptor#CheckLoop(String, SimpleInOutParameter)}. The processing 
	 * is performed using external filters defined in the Workflow Engine which are passed to the 
	 * {@link ParameterExternalFilter}. These are the {@link JobStatusExternalFilter}, the {@link JobStatusBreakLoopFilter}
	 * and {@link JobLogInfoFilter} and are used in this order.
	 * 
	 * @param StatusOutputVariableID the status output variable id
	 * @param LoggingOutputVariableID the logging output variable id
	 * @param BreakParameterName the break parameter name
	 * @param StatusCheckNodeID the status check node id
	 * 
	 * @return the parameter processing plan element
	 */
	private ParameterProcessingPlanElement ProcessOutputs(String StatusOutputVariableID,String LoggingOutputVariableID,String BreakParameterName,String StatusCheckNodeID,String SuccessParameterName)
	{
		ParameterProcessingPlanElement proc=new ParameterProcessingPlanElement();

		ParameterExternalFilter statusFilter=new ParameterExternalFilter();
		statusFilter.Order=0;
		statusFilter.TokenMapping.clear();
		statusFilter.ExternalFilter=new JobStatusExternalFilter();
		((JobStatusExternalFilter)statusFilter.ExternalFilter).JobStatusVariableName=StatusOutputVariableID;
		NamedDataType ndtJobStatus=new NamedDataType();
		ndtJobStatus.IsAvailable=false;
		ndtJobStatus.Name=UUID.randomUUID().toString();
		ndtJobStatus.Token=ndtJobStatus.Name;
		ndtJobStatus.Value=new DataTypeString();
		this.Plan.Variables.Add(ndtJobStatus);
		((JobStatusExternalFilter)statusFilter.ExternalFilter).JobStatusOutputVariableName=ndtJobStatus.Name;
		((JobStatusExternalFilter)statusFilter.ExternalFilter).StoreOutput=true;

		ParameterExternalFilter parseStatusFilter=new ParameterExternalFilter();
		parseStatusFilter.Order=1;
		parseStatusFilter.TokenMapping.clear();
		parseStatusFilter.ExternalFilter=new JobStatusBreakLoopFilter();
		((JobStatusBreakLoopFilter)parseStatusFilter.ExternalFilter).JobStatusVariableName=ndtJobStatus.Name;
		((JobStatusBreakLoopFilter)parseStatusFilter.ExternalFilter).JobStatusOutputVariableName=BreakParameterName;
		((JobStatusBreakLoopFilter)parseStatusFilter.ExternalFilter).StoreOutput=true;

		ParameterExternalFilter parseSuccessFilter=new ParameterExternalFilter();
		parseSuccessFilter.Order=2;
		parseSuccessFilter.TokenMapping.clear();
		parseSuccessFilter.ExternalFilter=new JobStatusSuccessFilter();
		((JobStatusSuccessFilter)parseSuccessFilter.ExternalFilter).JobStatusVariableName=ndtJobStatus.Name;
		((JobStatusSuccessFilter)parseSuccessFilter.ExternalFilter).JobStatusOutputVariableName=SuccessParameterName;
		((JobStatusSuccessFilter)parseSuccessFilter.ExternalFilter).StoreOutput=true;
		
		ParameterExternalFilter parseLoggingInfoFilter=new ParameterExternalFilter();
		parseLoggingInfoFilter.Order=3;
		parseLoggingInfoFilter.TokenMapping.clear();
		parseLoggingInfoFilter.ExternalFilter=new JobLogInfoFilter();
		((JobLogInfoFilter)parseLoggingInfoFilter.ExternalFilter).JobLogInfoVariableName=LoggingOutputVariableID;
		((JobLogInfoFilter)parseLoggingInfoFilter.ExternalFilter).PlanNodeID=StatusCheckNodeID;
		
		FilteredInParameter procParam=new FilteredInParameter();
		procParam.Filters.add(statusFilter);
		procParam.Filters.add(parseStatusFilter);
		procParam.Filters.add(parseSuccessFilter);
		procParam.Filters.add(parseLoggingInfoFilter);
		proc.Parameters.add(procParam);
		return proc;
	}
	
	/**
	 * Compress the output files after the job is completed
	 * 
	 * @param compressedName the name of the compressed file
	 * @param OutputDirectory the output directory , whose files to compress
	 * 
	 * @return the shell plan element that retrieves the output
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ShellPlanElement CompressOutput(String compressedName, String OutputDirectory) throws WorkflowValidationException
	{
		try {
			ShellPlanElement sts = new ShellPlanElement();
			
			sts.Command = "/bin/tar";
			sts.SetName("tar");
			
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0)
				throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0)
				throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			
			sts.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			sts.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			sts.Triggers.clear();
			
			sts.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not retrieve job output",MapType.NotEqual));
			sts.StdErrIsFile=false;
			sts.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			sts.StdInIsFile=false;
			sts.StdInParameter=null;
			sts.StdOutIsFile=false;
			
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-cvf", this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(OutputDirectory + "/" + compressedName + ".tar", this.Plan), true));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(OutputDirectory, this.Plan), true));
			
			return sts;
		}
		catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Cancel the job
	 * 
	 * @param jobIDFile the job id file
	 * 
	 * @return the shell plan element that cancels the job
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ShellPlanElement CancelJob(String jobIDFile) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement sts=new ShellPlanElement();
			sts.SetName("glite-wms-job-cancel");
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");

			sts.Command=glite_location_string +"/bin/glite-wms-job-cancel";
			sts.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			sts.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			sts.Triggers.clear();
			sts.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not cancel the job",MapType.NotEqual));
			sts.StdErrIsFile=false;
			sts.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			sts.StdInIsFile=false;
			sts.StdInParameter=null;
			sts.StdOutIsFile=false;
			sts.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--noint",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-i",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(jobIDFile,this.Plan),true));
			return sts;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Retrieves the status of the job
	 * 
	 * @param jobIDFile the job id file
	 * @param OutputVariableID the output variable id
	 * 
	 * @return the shell plan element that retrieves the status of the job
	 * 
	 * @throws WorkflowValidationException the workflow validation exception
	 */
	private ShellPlanElement CheckStatus(String jobIDFile,String OutputVariableID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement sts=new ShellPlanElement();
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			sts.Command=glite_location_string+"/bin/glite-wms-job-status";
			sts.SetName("glite-wms-job-status");
			sts.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			sts.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			ContingencyTrigger trig= new ContingencyTrigger();
			trig.IsFullNameOfError=false;
			trig.TriggeringError=null;
			trig.Reaction=new ContingencyReactionRetry();
			((ContingencyReactionRetry)trig.Reaction).NumberOfRetries=this.RetryOnErrorTimes;
			((ContingencyReactionRetry)trig.Reaction).RetryInterval=this.RetryOnErrorPeriod;
			sts.Triggers.add(trig);
			sts.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not check status of the job",MapType.NotEqual));
			sts.StdErrIsFile=false;
			sts.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			sts.StdInIsFile=false;
			sts.StdInParameter=null;
			sts.StdOutIsFile=false;
			sts.StdOutParameter=AdaptorUtils.GetInOutPrameter(OutputVariableID,this.Plan);
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-i",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(jobIDFile,this.Plan),true));
			return sts;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Retrieve the output files after the job is completed
	 * 
	 * @param jobIDFile the job id file
	 * @param OutputDirectory the output directory to store the files
	 * 
	 * @return the shell plan element that retrieves the output
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ShellPlanElement RetrieveOutput(String jobIDFile,String OutputDirectory) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement sts=new ShellPlanElement();
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			sts.Command=glite_location_string+"/bin/glite-wms-job-output";
			sts.SetName("glite-wms-job-output");
			sts.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			sts.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			logger.debug("X509_USER_PROXY env var: " + this.Resources.GetUserProxyResource().Key);
			logger.debug("GLOBUS_LOCATIOIN env var: " + globus_location_string);
			sts.Triggers.clear();
			sts.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not retrieve job output",MapType.NotEqual));
			sts.StdErrIsFile=false;
			sts.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			sts.StdInIsFile=false;
			sts.StdInParameter=null;
			sts.StdOutIsFile=false;
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--noint",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--nosubdir",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--dir",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(OutputDirectory,this.Plan),true));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-i",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(jobIDFile,this.Plan),true));
			return sts;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Retr4ieves the logging info of the job
	 * 
	 * @param jobIDFile the job id file
	 * @param OutputVariableID the output variable id
	 * 
	 * @return the shell plan element that retrieves the logging info
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ShellPlanElement CheckLoggingInfo(String jobIDFile,String OutputVariableID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement sts=new ShellPlanElement();
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			sts.Command=glite_location_string+"/bin/glite-wms-job-logging-info";
			sts.SetName("glite-wms-job-logging-info");
			sts.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			sts.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			ContingencyTrigger trig= new ContingencyTrigger();
			trig.IsFullNameOfError=false;
			trig.TriggeringError=null;
			trig.Reaction=new ContingencyReactionRetry();
			((ContingencyReactionRetry)trig.Reaction).NumberOfRetries=this.RetryOnErrorTimes;
			((ContingencyReactionRetry)trig.Reaction).RetryInterval=this.RetryOnErrorPeriod;
			sts.Triggers.add(trig);
			sts.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not check logging info of the job",MapType.NotEqual));
			sts.StdErrIsFile=false;
			sts.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			sts.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			sts.StdInIsFile=false;
			sts.StdInParameter=null;
			sts.StdOutIsFile=false;
			sts.StdOutParameter=AdaptorUtils.GetInOutPrameter(OutputVariableID,this.Plan);
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-i",this.Plan)));
			sts.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(jobIDFile,this.Plan),true));
			return sts;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Construct submit job element.
	 * 
	 * @param jobIDFile the job id file
	 * 
	 * @return the shell plan element that submits the job
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private ShellPlanElement ConstructSubmitJobElement(String jobIDFile) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			String glite_location_string=this.GridUINode.getExtension("glite.glite_location");
			if(glite_location_string==null || glite_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			String globus_location_string=this.GridUINode.getExtension("glite.globus_location");
			if(globus_location_string==null || globus_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property glite.globus_location set in globus.gateway node");
			subm.Command=glite_location_string+"/bin/glite-wms-job-submit";
			subm.SetName("glite-wms-job-submit");
			subm.Environment.add(new EnvironmentKeyValue("X509_USER_PROXY", this.Resources.GetUserProxyResource().Key));
			subm.Environment.add(new EnvironmentKeyValue("GLOBUS_LOCATION", globus_location_string));
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not submit the job",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			if(this.Resources.GetConfigResource()!=null)
			{
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--config",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetConfigResource().Key,this.Plan),true));
			}
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-a",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("--output",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(jobIDFile,this.Plan),true));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetJDLResource().Key,this.Plan),true));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Construct the boundary element that will be located in the grid UI node
	 * 
	 * @return the boundary plan element
	 * 
	 * @throws WorkflowEnvironmentException An environment error occurred
	 */
	private BoundaryPlanElement ConstructBoundaryElement() throws WorkflowEnvironmentException
	{
		BoundaryPlanElement  bound=new BoundaryPlanElement();
		this.Plan.Root=bound;
		bound.CleanUpLocalFiles.clear();
		bound.Triggers.clear();
		bound.Config=this.GetBoundaryConfig();
		bound.Isolation=new BoundaryIsolationInfo();
		bound.Isolation.Isolate=true;
		bound.Isolation.CleanUp=true;
		bound.Isolation.BaseDir=new SimpleInOutParameter();
		NamedDataType ndtIsolationBaseDirParameter=new NamedDataType();
		ndtIsolationBaseDirParameter.IsAvailable=false;
		ndtIsolationBaseDirParameter.Name=UUID.randomUUID().toString();
		ndtIsolationBaseDirParameter.Token=ndtIsolationBaseDirParameter.Name;
		ndtIsolationBaseDirParameter.Value=new DataTypeString();
		this.Plan.Variables.Add(ndtIsolationBaseDirParameter);
		((SimpleInOutParameter)bound.Isolation.BaseDir).VariableName=ndtIsolationBaseDirParameter.Name;
		ExecutionAttachment att=new ExecutionAttachment();
		att.CleanUpRestored=true;
		att.LocationType=AttachmentLocation.LocalFile;
		att.LocationValue=this.Resources.GetUserProxyResource().Value;
		att.RestoreLocationValue=this.Resources.GetUserProxyResource().Key;
		att.Permissions="0600";
		bound.Attachments.add(att);
		return bound;
	}
	
	/**
	 * Gets the boundary config that will be used in the boundary element that is constructed by
	 * {@link WorkflowGridAdaptor#ConstructBoundaryElement()}
	 * 
	 * @return the boundary config
	 * 
	 * @throws WorkflowEnvironmentException the workflow environment exception
	 */
	private BoundaryConfig GetBoundaryConfig() throws WorkflowEnvironmentException
	{
		try
		{
			this.GridUINode=InformationSystem.GetMatchingNode(null,"glite.gateway == true" , Hints);
			if(this.GridUINode==null) throw new WorkflowEnvironmentException("Could not find appropriate grid UI node to host execution");
			logger.info("Selected Execution Engine: " + this.GridUINode.getExtension("hostname") + ":" + this.GridUINode.getExtension("pe2ng.port"));
		}catch(Exception ex)
		{
			throw new WorkflowEnvironmentException("Could not retrieve environment information from Information System", ex);
		}
		BoundaryConfig Config=new BoundaryConfig();
		Config.HostName=this.GridUINode.getExtension("hostname");
		Config.Port=Integer.parseInt(this.GridUINode.getExtension("pe2ng.port"));
		Config.NozzleConfig=new TCPServerNozzleConfig(false, 0);
		return Config;
	}
	
	/**
	 * Creates a store file element that will store the provided file in the Storage System
	 * 
	 * @param FileName the file name to store
	 * 
	 * @return the file transfer plan element
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private FileTransferPlanElement CreateStoreFileElement(String FileName) throws WorkflowValidationException
	{
		try
		{
			FileTransferPlanElement ftr=new FileTransferPlanElement();
			ftr.Direction=TransferDirection.Store;
			ftr.Input=AdaptorUtils.GetInParameter(FileName,this.Plan);
			ftr.IsExecutable=false;
			ftr.MoveTo=null;
			ftr.Output=AdaptorUtils.GetOutPrameter(this.Plan);
			return ftr;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	/**
	 * Creates a store file element that will store to the Storage System the file that is contained in the
	 * provided directory and is described by the provided attachment resource
	 * 
	 * @param attachment The resource describing the file to store
	 * @param OutputDirectory The output directory that contains the file to store
	 * 
	 * @return the file transfer plan element
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	private FileTransferPlanElement CreateStoreFileElement(AttachedGridResource attachment,String OutputDirectory) throws WorkflowValidationException
	{
		try
		{
			FileTransferPlanElement ftr=new FileTransferPlanElement();
			ftr.Direction=TransferDirection.Store;
			ftr.Input=AdaptorUtils.GetInParameter(OutputDirectory+"/"+attachment.Key,this.Plan);
			if(attachment.ResourceLocationType == AttachedResourceType.Reference)
			{
				boolean slashFound = attachment.Value.charAt(attachment.Value.length()-1) == '/';
				ftr.OutputStoreMode = StoreMode.Url;
				//ftr.StoreUrlLocation = attachment.Value + "/" + (this.ExecutionId != null ? this.ExecutionId + "/": "") + attachment.Key;
				ftr.StoreUrlLocation = attachment.Value + (slashFound ? "" : "/") + (this.ExecutionId != null ? this.ExecutionId + "." : "" ) + attachment.Key;
				ftr.accessInfo.port = attachment.accessInfo.port;
				ftr.accessInfo.userId = attachment.accessInfo.userId;
				ftr.accessInfo.password = attachment.accessInfo.password;
			}
			ftr.IsExecutable=false;
			ftr.MoveTo=null;
			SimpleOutParameter outParam=AdaptorUtils.GetOutPrameter(this.Plan);
			ftr.Output=outParam;
			OutputSandboxGridResource out=new OutputSandboxGridResource();
			out.Key=attachment.Key;
			out.VariableID=outParam.VariableName;
			this.OutputResources.add(out);
			return ftr;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct store file element",ex);
		}
	}
		
	/**
	 * Creates a file element that can retrieve the file described by the provided attachment resource
	 * and rename it accordingly
	 * 
	 * @param attachment the attachment describing the resource to retrieve
	 * 
	 * @return the file transfer plan element
	 * 
	 * @throws WorkflowValidationException the workflow validation exception
	 */
	private FileTransferPlanElement CreateRetrieveFileElement(AttachedGridResource attachment) throws WorkflowValidationException
	{
		FileTransferPlanElement ftr=new FileTransferPlanElement();
		ftr.Direction=TransferDirection.Retrieve;
		ftr.Input=new SimpleInParameter();
		ftr.IsExecutable=false;
		NamedDataType ndtAttachment=new NamedDataType();
		ndtAttachment.IsAvailable=true;
		ndtAttachment.Name=UUID.randomUUID().toString();
		ndtAttachment.Token=ndtAttachment.Name;
		ndtAttachment.Value=new DataTypeString();
		this.Plan.Variables.Add(ndtAttachment);
		try
		{
			((DataTypeString)ndtAttachment.Value).SetValue(attachment.StorageSystemID);
		}catch(Exception ex)
		{
			throw new WorkflowValidationException("Could not create execution plan",ex);
		}
		((SimpleInParameter)ftr.Input).VariableName=ndtAttachment.Name;
		ftr.Output=new SimpleOutParameter();
		NamedDataType ndtRetrievedAttachment=new NamedDataType();
		ndtRetrievedAttachment.IsAvailable=false;
		ndtRetrievedAttachment.Name=UUID.randomUUID().toString();
		ndtRetrievedAttachment.Token=ndtAttachment.Name;
		ndtRetrievedAttachment.Value=new DataTypeString();
		this.Plan.Variables.Add(ndtRetrievedAttachment);
		((SimpleOutParameter)ftr.Output).VariableName=ndtRetrievedAttachment.Name;
		ftr.MoveTo=new SimpleInParameter();
		NamedDataType ndtRename=new NamedDataType();
		ndtRename.IsAvailable=true;
		ndtRename.Name=UUID.randomUUID().toString();
		ndtRename.Token=ndtAttachment.Name;
		ndtRename.Value=new DataTypeString();
		String RenameTo=attachment.Key;
		if(RenameTo==null || RenameTo.trim().length()==0) throw new WorkflowValidationException("Defined resource name is not valid");
		try
		{
			((DataTypeString)ndtRename.Value).SetValue(RenameTo);
		}catch(Exception ex)
		{
			throw new WorkflowValidationException("Could not create execution plan",ex);
		}
		this.Plan.Variables.Add(ndtRename);
		((SimpleInParameter)ftr.MoveTo).VariableName=ndtRename.Name;
		return ftr;
	}
}
