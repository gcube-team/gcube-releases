package gr.uoa.di.madgik.workflow.adaptor;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.PlanConfig.ConnectionMode;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.BreakPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ConditionalPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.LoopPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ParameterProcessingPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WaitPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.condition.ArrayIterationPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.BooleanVariableCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeLeaf;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionalFlow;
import gr.uoa.di.madgik.execution.plan.element.condition.DecimalRangePlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.TimeOutPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode.NodeVerb;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode.PostVerb;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterEmitPayloadFilter;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterExternalFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping.MapType;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.FilteredInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.AdaptorCondorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.AttachedCondorResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.JobQueueOutputCheckExternalFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.JobSubmitExternalFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.OutputCondorResource;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowCondorAdaptor implements IWorkflowAdaptor
{
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkflowCondorAdaptor.class);
	
	private Set<IOutputResource> OutputResources=null;
	private AdaptorCondorResources Resources=null;
	private ExecutionPlan Plan=null;
	private String ExecutionId=null;
	private EnvHintCollection Hints=new EnvHintCollection();
	private NodeInfo CondorUINode=null;
	
	private static final Boolean DefaultRetrieveClassAds=false;
	private static long DefaultWaitPeriod=60*1000;
	private static long DefaultTimeout=Long.MAX_VALUE;
	private static boolean DefaultIsDag=false;
	
	public Boolean RetrieveJobClassAd=WorkflowCondorAdaptor.DefaultRetrieveClassAds;
	public long WaitPeriod=WorkflowCondorAdaptor.DefaultWaitPeriod;
	public long Timeout=WorkflowCondorAdaptor.DefaultTimeout;
	public boolean IsDag=WorkflowCondorAdaptor.DefaultIsDag;
	
	public WorkflowCondorAdaptor()
	{
		this.OutputResources=new HashSet<IOutputResource>();
	}

	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException
	{
		if(!(Resources instanceof AdaptorCondorResources)) throw new WorkflowValidationException("Invalid adaptor resources provided");
		this.Resources=(AdaptorCondorResources)Resources;
		this.Resources.Validate();
	}

	public void SetExecutionId(String executionId)
	{
		this.ExecutionId = executionId;
	}
	
	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException, WorkflowInternalErrorException, WorkflowEnvironmentException
	{
		if(this.Resources==null) throw new WorkflowValidationException("No resources specified");
		this.CreateEnvironmentHints();
		this.ConstructWorkflow();
		this.ExcludeOutputResourcesCleanUp();
	}
	
	private void ExcludeOutputResourcesCleanUp()
	{
		for(IOutputResource res : this.OutputResources)
		{
			if(!(res instanceof OutputCondorResource)) continue;
			this.Plan.CleanUpSSExclude.Add(((OutputCondorResource)res).VariableID);
		}
	}
	
	private void CreateEnvironmentHints()
	{
		AttachedCondorResource res= this.Resources.GetScopeResource();
		if(res!=null)
		{
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(res.Value)));
		}
	}

	public ExecutionPlan GetCreatedPlan()
	{
		return this.Plan;
	}

	public Set<IOutputResource> GetOutput()
	{
		return this.OutputResources;
	}
	
	private void ConstructWorkflow() throws WorkflowInternalErrorException,WorkflowValidationException, WorkflowEnvironmentException
	{
		// Store the resources that will be need to be moved to the Grid UI except the user proxy 
		try
		{
			Set<AttachedCondorResource.ResourceType> ResourcesToStore=new HashSet<AttachedCondorResource.ResourceType>();
			ResourcesToStore.add(AttachedCondorResource.ResourceType.InData);
			ResourcesToStore.add(AttachedCondorResource.ResourceType.Executable);
			ResourcesToStore.add(AttachedCondorResource.ResourceType.Submit);
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
		//All the execution will take place in the remote CONDOR UI node
		BoundaryPlanElement  bound=this.ConstructBoundaryElement();
		this.Plan.Root=bound;
		//All of the execution in the remote host will take place in a sequential order
		bound.Root=new SequencePlanElement();
		for(AttachedCondorResource att : this.Resources.GetInDataResources()) ((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(att));
		for(AttachedCondorResource att : this.Resources.GetExecutableResources()) ((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(att,true));
		((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(this.Resources.GetSubmitResource()));
		//Submit the job
		String IdentifierSerializationParameterID=AdaptorUtils.GetInOutPrameter(Plan).VariableName;
		ShellPlanElement submitJob=null;
		if(!this.IsDag) submitJob=this.ConstructSubmitJobElement(IdentifierSerializationParameterID);
		else submitJob=this.ConstructSubmitDagElement(IdentifierSerializationParameterID);
		((SequencePlanElement)bound.Root).ElementCollection.add(submitJob);
		//((SequencePlanElement)bound.Root).ElementCollection.add(this.ConstructSubmitJobElement(IdentifierSerializationParameterID));
		//Extract identifiers array
		String IdentifierArrayParameterID=UUID.randomUUID().toString();
		((SequencePlanElement)bound.Root).ElementCollection.add(this.ProcessJobIdentifier(IdentifierSerializationParameterID,IdentifierArrayParameterID));
		//start loop
		String LoopControlParameterID=UUID.randomUUID().toString();
		LoopPlanElement mainLoop=this.CreateLoop(LoopControlParameterID);
		mainLoop.Root=new SequencePlanElement();
		((SequencePlanElement)bound.Root).ElementCollection.add(mainLoop);
		//wait before checking status
		((SequencePlanElement)mainLoop.Root).ElementCollection.add(this.PauseToRecheck());
		//Check status and retrieve class add
		((SequencePlanElement)mainLoop.Root).ElementCollection.add(this.CheckArrayJobStatus(IdentifierArrayParameterID,LoopControlParameterID));
		IPlanElement timoutCheck=this.CheckTimeout(LoopControlParameterID,IdentifierArrayParameterID);
		if(timoutCheck!=null)((SequencePlanElement)bound.Root).ElementCollection.add(timoutCheck);
		//create, store and delete workspace archive
		TryCatchFinallyPlanElement tcf=new TryCatchFinallyPlanElement();
		((SequencePlanElement)bound.Root).ElementCollection.add(tcf);
		SequencePlanElement seqArchive=new SequencePlanElement();
		tcf.TryFlow=seqArchive;
		String archiveName="/tmp/"+UUID.randomUUID().toString()+".tar.gz";
		seqArchive.ElementCollection.add(this.CreateOutputArchive(archiveName));
		seqArchive.ElementCollection.add(this.CreateStoreFileElementToStorage(archiveName,OutputCondorResource.OutputType.OutputArchive));
		tcf.CatchFlows.clear();
		tcf.FinallyFlow=this.RemoveOutputArchive(archiveName);
	}

	private ConditionalPlanElement CheckTimeout(String LoopControlParameterID,String IdentifierArrayParameterID) throws WorkflowValidationException
	{
		if(this.Timeout==Long.MIN_VALUE || this.Timeout==Long.MAX_VALUE || this.Timeout==0) return null;
		ConditionalPlanElement cond=new ConditionalPlanElement();
		cond.IfFlow=this.GetTimeoutConditionalFlow(LoopControlParameterID,IdentifierArrayParameterID);
		cond.ElseIfFlows.clear();
		cond.ElseFlow=null;
		return cond;
	}
	
	private ConditionalFlow GetTimeoutConditionalFlow(String LoopControlParameterID,String IdentifierArrayParameterID) throws WorkflowValidationException
	{
		ConditionalFlow cond= new ConditionalFlow();
		cond.Condition=new ConditionTree();
		cond.Condition.Root=new ConditionTreeNode();
		((ConditionTreeNode)cond.Condition.Root).Verb=NodeVerb.AND;
		((ConditionTreeNode)cond.Condition.Root).Post=PostVerb.Negate;
		ConditionTreeLeaf boolCond=new ConditionTreeLeaf();
		boolCond.Condition=new BooleanVariableCondition();
		((BooleanVariableCondition)boolCond.Condition).FlagParameter=AdaptorUtils.GetInParameter(LoopControlParameterID);
		((ConditionTreeNode)cond.Condition.Root).Childen.add(boolCond);
		cond.Root=new SequencePlanElement();
		((SequencePlanElement)cond.Root).ElementCollection.add(this.CancelArrayJobStatus(IdentifierArrayParameterID));
		BreakPlanElement br= new BreakPlanElement();
		br.Message="Timeout while waiting for job to complete";
		((SequencePlanElement)cond.Root).ElementCollection.add(br);
		return cond;
	}

	private WaitPlanElement PauseToRecheck()
	{
		WaitPlanElement wait=new WaitPlanElement();
		wait.WaitPeriod=this.WaitPeriod;
		return wait;
	}
	
	private SequencePlanElement CheckArrayJobStatus(String IdentifierArrayParameterID,String LoopControlParameterID) throws WorkflowValidationException
	{
		try{
			String OutputParameterID=AdaptorUtils.GetInParameter("",Plan).VariableName;
			String UpdateOutputParameterID=AdaptorUtils.GetInParameter("",Plan).VariableName;
			String CurrentArrayIdentifierParameterID=AdaptorUtils.GetInOutPrameter(Plan).VariableName;
			
			SequencePlanElement seq=new SequencePlanElement();
			
			seq.ElementCollection.add(this.CheckJobStatusOutput(OutputParameterID, UpdateOutputParameterID, true,false));
	
			LoopPlanElement loop=new LoopPlanElement();
			loop.Root=new SequencePlanElement();
			loop.LoopCondition=new ConditionTree();
			loop.LoopCondition.Root=new ConditionTreeLeaf();
			((ConditionTreeLeaf)loop.LoopCondition.Root).Condition=new ArrayIterationPlanCondition();
			((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).ArrayParameter=AdaptorUtils.GetInParameter(IdentifierArrayParameterID);
			((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).CurrentArrayValueParameter=AdaptorUtils.GetInOutPrameter(CurrentArrayIdentifierParameterID,Plan);
			((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).CurrentValueParameter=AdaptorUtils.GetInOutPrameter(Plan);
			((SequencePlanElement)loop.Root).ElementCollection.add(this.CheckJobStatus(CurrentArrayIdentifierParameterID,OutputParameterID));
			((SequencePlanElement)loop.Root).ElementCollection.add(this.CheckJobStatusOutput(OutputParameterID, UpdateOutputParameterID,false,false));
			if(this.RetrieveJobClassAd)
			{
				String ClassAdParameterID=AdaptorUtils.GetInOutPrameter(Plan).VariableName;
				ShellPlanElement retrieveClassAdd=this.GetJobClassAd(CurrentArrayIdentifierParameterID,ClassAdParameterID);
				((SequencePlanElement)loop.Root).ElementCollection.add(retrieveClassAdd);
				((SequencePlanElement)loop.Root).ElementCollection.add(this.EmitJobClassAd(ClassAdParameterID, retrieveClassAdd.GetID()));
			}
			seq.ElementCollection.add(loop);
			
			seq.ElementCollection.add(this.CheckJobStatusOutput(UpdateOutputParameterID, LoopControlParameterID, false,true));
			
			return seq;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct array identifier check script",ex);
		}
	}
	
	private LoopPlanElement CancelArrayJobStatus(String IdentifierArrayParameterID) throws WorkflowValidationException
	{
		String CurrentArrayIdentifierParameterID=AdaptorUtils.GetInOutPrameter(Plan).VariableName;
		LoopPlanElement loop=new LoopPlanElement();
		loop.Root=new SequencePlanElement();
		loop.LoopCondition=new ConditionTree();
		loop.LoopCondition.Root=new ConditionTreeLeaf();
		((ConditionTreeLeaf)loop.LoopCondition.Root).Condition=new ArrayIterationPlanCondition();
		((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).ArrayParameter=AdaptorUtils.GetInParameter(IdentifierArrayParameterID);
		((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).CurrentArrayValueParameter=AdaptorUtils.GetInOutPrameter(CurrentArrayIdentifierParameterID,Plan);
		((ArrayIterationPlanCondition)((ConditionTreeLeaf)loop.LoopCondition.Root).Condition).CurrentValueParameter=AdaptorUtils.GetInOutPrameter(Plan);
		((SequencePlanElement)loop.Root).ElementCollection.add(this.CancelJob(CurrentArrayIdentifierParameterID));
		return loop;
	}
	
	private ParameterProcessingPlanElement CheckJobStatusOutput(String OutputParameterID,String UpdateOutputParameterID,Boolean ClearUpdate,Boolean FinalOutcome)
	{
		ParameterExternalFilter checkQueueOutputFilter=new ParameterExternalFilter();
		checkQueueOutputFilter.Order=1;
		checkQueueOutputFilter.TokenMapping.clear();
		checkQueueOutputFilter.ExternalFilter=new JobQueueOutputCheckExternalFilter();
		((JobQueueOutputCheckExternalFilter)checkQueueOutputFilter.ExternalFilter).ClearUp=ClearUpdate;
		((JobQueueOutputCheckExternalFilter)checkQueueOutputFilter.ExternalFilter).FinalOutcome=FinalOutcome;
		((JobQueueOutputCheckExternalFilter)checkQueueOutputFilter.ExternalFilter).StoreOutput=true;
		((JobQueueOutputCheckExternalFilter)checkQueueOutputFilter.ExternalFilter).JobOutputVariableName=OutputParameterID;
		((JobQueueOutputCheckExternalFilter)checkQueueOutputFilter.ExternalFilter).JobOutputUpdateVariableName=UpdateOutputParameterID;
		
		ParameterProcessingPlanElement proc=new ParameterProcessingPlanElement();
		FilteredInParameter procParam=new FilteredInParameter();
		procParam.Filters.add(checkQueueOutputFilter);
		proc.Parameters.add(procParam);

		return proc;
	}
	
	private ParameterProcessingPlanElement EmitJobClassAd(String ClassAdParameterID,String PlanElementID)
	{
		ParameterEmitPayloadFilter emitFilter=new ParameterEmitPayloadFilter();
		emitFilter.Order=1;
		emitFilter.PlanNodeID=PlanElementID;
		emitFilter.TokenMapping.clear();
		emitFilter.EmitVariableName=ClassAdParameterID;
		
		ParameterProcessingPlanElement proc=new ParameterProcessingPlanElement();
		FilteredInParameter procParam=new FilteredInParameter();
		procParam.Filters.add(emitFilter);
		proc.Parameters.add(procParam);

		return proc;
	}
	
	private LoopPlanElement CreateLoop(String LoopControlParameterID) throws WorkflowValidationException
	{
		try
		{
			LoopPlanElement loop=new LoopPlanElement();
			ConditionTree condt=new ConditionTree();
			loop.LoopCondition=condt;
			if(this.Timeout==Long.MIN_VALUE || this.Timeout==Long.MAX_VALUE || this.Timeout==0)
			{
				condt.Root=new ConditionTreeLeaf();
				((ConditionTreeLeaf)condt.Root).Condition=new BooleanVariableCondition();
				((BooleanVariableCondition)((ConditionTreeLeaf)condt.Root).Condition).FlagParameter=AdaptorUtils.GetInOutParameter(LoopControlParameterID,true, this.Plan);
			}
			else
			{
				condt.Root=new ConditionTreeNode();
				((ConditionTreeNode)condt.Root).Verb=NodeVerb.AND;
				
				ConditionTreeLeaf jobDone=new ConditionTreeLeaf();
				jobDone.Condition=new BooleanVariableCondition();
				((BooleanVariableCondition)jobDone.Condition).FlagParameter=AdaptorUtils.GetInOutParameter(LoopControlParameterID,true, this.Plan);
				
				ConditionTreeLeaf jobTimedOut=new ConditionTreeLeaf();
				jobTimedOut.Condition=new TimeOutPlanCondition();
				((TimeOutPlanCondition)jobTimedOut.Condition).TimeoutThreshold=this.Timeout;
				
				((ConditionTreeNode)condt.Root).Childen.add(jobDone);
				((ConditionTreeNode)condt.Root).Childen.add(jobTimedOut);
			}
			return loop;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}

	private BoundaryPlanElement ConstructBoundaryElement() throws WorkflowEnvironmentException
	{
		BoundaryPlanElement  bound=new BoundaryPlanElement();
		this.Plan.Root=bound;
		bound.CleanUpLocalFiles.clear();
		bound.Triggers.clear();
		bound.Attachments.clear();
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
		return bound;
	}

	private BoundaryConfig GetBoundaryConfig() throws WorkflowEnvironmentException
	{
//		BoundaryListenerInfo listenerInNode=null;
		try
		{
			this.CondorUINode=InformationSystem.GetMatchingNode(null, "condor.gateway == true", Hints);
//			this.CondorUINode=InformationSystem.GetRandomCondorUINodeContainingListener(this.Hints);
			if(this.CondorUINode==null) throw new WorkflowEnvironmentException("Could not find appropriate condor UI node to host execution");
			logger.info("Selected Execution Engine: " + this.CondorUINode.getExtension("hostname") + ":" + this.CondorUINode.getExtension("pe2ng.port"));
//			listenerInNode=InformationSystem.GetBoundaryListenerInNode(this.CondorUINode.ID,this.Hints);
//			if(listenerInNode==null) throw new WorkflowEnvironmentException("Could not find appropriate node to host execution");
		}catch(Exception ex)
		{
			throw new WorkflowEnvironmentException("Could not retrieve environment information from Information System", ex);
		}
		BoundaryConfig Config=new BoundaryConfig();
		Config.HostName=this.CondorUINode.getExtension("hostname");
		Config.Port=Integer.parseInt(this.CondorUINode.getExtension("pe2ng.port"));
		Config.NozzleConfig=new TCPServerNozzleConfig(false, 0);
		return Config;
	}
	
	private FileTransferPlanElement CreateRetrieveFileElement(AttachedCondorResource attachment) throws WorkflowValidationException
	{
		return this.CreateRetrieveFileElement(attachment, false);
	}
	
	private FileTransferPlanElement CreateRetrieveFileElement(AttachedCondorResource attachment,Boolean isExecutable) throws WorkflowValidationException
	{
		FileTransferPlanElement ftr=new FileTransferPlanElement();
		ftr.Direction=TransferDirection.Retrieve;
		ftr.Input=new SimpleInParameter();
		ftr.IsExecutable=isExecutable;
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
	
	private ShellPlanElement ConstructSubmitJobElement(String IdentifierParameterID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			String condor_location_string=this.CondorUINode.getExtension("condor.condor_location");
			if(condor_location_string==null || condor_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property condor.condor_location not set in condor.gateway node");
			subm.Command=condor_location_string+"/condor_submit";
			subm.SetName("condor_submit");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not submit the job",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(IdentifierParameterID,this.Plan);
			for(AttachedCondorResource att : this.Resources.GetCommandResources())
			{
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-a",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("\""+att.Value+"\"",this.Plan)));
			}
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetSubmitResource().Key,this.Plan),true));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	private ShellPlanElement ConstructSubmitDagElement(String IdentifierParameterID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			String condor_location_string=this.CondorUINode.getExtension("condor.condor_location");
			if(condor_location_string==null || condor_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property condor.condor_location not set in condor.gateway node");
			subm.Command=condor_location_string+"/condor_submit_dag";
			subm.SetName("condor_submit_dag");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not submit the job",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(IdentifierParameterID,this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetSubmitResource().Key,this.Plan),true));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	private ParameterProcessingPlanElement ProcessJobIdentifier(String IdentifierParameterID,String IdentifierArrayParameterID)
	{
		NamedDataType ndtIDs=new NamedDataType();
		ndtIDs.IsAvailable=false;
		ndtIDs.Name=IdentifierArrayParameterID;
		ndtIDs.Token=ndtIDs.Name;
		ndtIDs.Value=new DataTypeArray();
		((DataTypeArray)ndtIDs.Value).SetArrayClassCode("["+IDataType.DataTypes.String);
		this.Plan.Variables.Add(ndtIDs);

		ParameterExternalFilter parseJobIdentifier=new ParameterExternalFilter();
		parseJobIdentifier.Order=1;
		parseJobIdentifier.TokenMapping.clear();
		parseJobIdentifier.ExternalFilter=new JobSubmitExternalFilter();
		((JobSubmitExternalFilter)parseJobIdentifier.ExternalFilter).JobIdentifierVariableName=IdentifierParameterID;
		((JobSubmitExternalFilter)parseJobIdentifier.ExternalFilter).JobIdentifierOutputVariableName=IdentifierArrayParameterID;
		((JobSubmitExternalFilter)parseJobIdentifier.ExternalFilter).StoreOutput=true;

		ParameterProcessingPlanElement proc=new ParameterProcessingPlanElement();
		FilteredInParameter procParam=new FilteredInParameter();
		procParam.Filters.add(parseJobIdentifier);
		proc.Parameters.add(procParam);

		return proc;
	}

	private ShellPlanElement CheckJobStatus(String IdentifierArrayCurrentParameterID,String OutputParameterID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			String condor_location_string=this.CondorUINode.getExtension("condor.condor_location");
			if(condor_location_string==null || condor_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property condor.condor_location not set in condor.gateway node");
			subm.Command=condor_location_string+"/condor_q";
			subm.SetName("condor_q status");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not check job status",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(OutputParameterID,this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(IdentifierArrayCurrentParameterID)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-format",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("\"%s\n\"",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("ClusterId",this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}

	private ShellPlanElement GetJobClassAd(String IdentifierArrayCurrentParameterID,String ClassAdParameterID) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			String condor_location_string=this.CondorUINode.getExtension("condor.condor_location");
			if(condor_location_string==null || condor_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property condor.condor_location not set in condor.gateway node");
			subm.Command=condor_location_string+"/condor_q";
			subm.SetName("condor_q class ad");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not check job status",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(ClassAdParameterID,this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(IdentifierArrayCurrentParameterID)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-xml",this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}

	private ShellPlanElement CancelJob(String IdentifierArrayCurrentParameterID) throws WorkflowValidationException
	{
		ShellPlanElement subm=new ShellPlanElement();
		String condor_location_string=this.CondorUINode.getExtension("condor.condor_location");
		if(condor_location_string==null || condor_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property condor.condor_location not set in condor.gateway node");
		subm.Command=condor_location_string+"/condor_rm";
		subm.SetName("condor_rm");
		subm.Environment.clear();
		subm.Triggers.clear();
		subm.ExitCodeErrors.clear();
		subm.StdErrIsFile=false;
		subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
		subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
		subm.StdInIsFile=false;
		subm.StdInParameter=null;
		subm.StdOutIsFile=false;
		subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
		subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(IdentifierArrayCurrentParameterID)));
		return subm;
	}
	
	private SequencePlanElement CreateStoreFileElementToStorage(String filename,OutputCondorResource.OutputType TypeOfOutput) throws WorkflowValidationException
	{
		try
		{
			SequencePlanElement seqInternal=new SequencePlanElement();
			
			ShellPlanElement lschck=new ShellPlanElement();
			lschck.Command="/bin/ls";
			lschck.SetName("archive exists");
			lschck.Environment.clear();
			lschck.Triggers.clear();
			lschck.ExitCodeErrors.clear();
			lschck.StdErrIsFile=false;
			lschck.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			lschck.StdExitValueParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			lschck.StdInIsFile=false;
			lschck.StdInParameter=null;
			lschck.StdOutIsFile=false;
			lschck.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(filename,this.Plan)));
			seqInternal.ElementCollection.add(lschck);
			
			FileTransferPlanElement ftr=new FileTransferPlanElement();
			ftr.Direction=TransferDirection.Store;
			try
			{
				ftr.Input=AdaptorUtils.GetInParameter(filename, this.Plan);
			} catch (ExecutionValidationException e)
			{
				throw new WorkflowValidationException("Could not costruct retrieve file from execution node", e);
			}
			ftr.IsExecutable=false;
			ftr.MoveTo=null;
			ftr.Permissions=null;
			ftr.Output=AdaptorUtils.GetOutPrameter(this.Plan);
			SimpleOutParameter outParam=AdaptorUtils.GetOutPrameter(this.Plan);
			ftr.Output=outParam;
			OutputCondorResource out=new OutputCondorResource();
			out.TypeOfOutput=TypeOfOutput;
			out.Key=filename;
			out.VariableID=outParam.VariableName;
			this.OutputResources.add(out);
			
			ConditionalPlanElement condElem=new ConditionalPlanElement();
			condElem.IfFlow=new ConditionalFlow();
			condElem.IfFlow.Root=ftr;
			condElem.IfFlow.Condition=new ConditionTree();
			condElem.IfFlow.Condition.Root=new ConditionTreeLeaf();
			((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition=new DecimalRangePlanCondition();
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).CurrentValueParameter=((SimpleInOutParameter)lschck.StdExitValueParameter);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).LeftBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RightBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeStartParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeEndParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			
			seqInternal.ElementCollection.add(condElem);
	
			return seqInternal;
		} catch (ExecutionValidationException e)
		{
			throw new WorkflowValidationException("Could not create store item", e);
		}
	}
	
	private ShellPlanElement CreateOutputArchive(String archiveName) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command="/bin/tar";
			subm.SetName("create output archive for workspace");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not create output archive",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-zcvf",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(archiveName,this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(".",this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}
	
	private ShellPlanElement RemoveOutputArchive(String archiveName) throws WorkflowValidationException
	{
		try
		{
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command="/bin/rm";
			subm.SetName("create output archive for workspace");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.clear();
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-f",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(archiveName,this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}
}
