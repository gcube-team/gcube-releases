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
import gr.uoa.di.madgik.execution.plan.element.ConditionalPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.StoreMode;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTree;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeLeaf;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionalFlow;
import gr.uoa.di.madgik.execution.plan.element.condition.DecimalRangePlanCondition;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterExternalFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping.MapType;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInOutParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleOutParameter;
import gr.uoa.di.madgik.execution.plan.trycatchfinally.CatchElement;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.AdaptorUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AdaptorHadoopResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.HadoopInOutDirectoryInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.HadoopInOutDirectoryInfo.OutStoreMode;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.HadoopVerboseProgressOutputFilter;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.OutputHadoopResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.OutputHadoopResource.OutputType;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowSerializationException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHadoopAdaptor implements IWorkflowAdaptor
{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(WorkflowHadoopAdaptor.class);
	
	private AdaptorHadoopResources Resources=null;
	private Set<IOutputResource> OutputResources=null;
	private ExecutionPlan Plan=null;
	private String ExecutionId=null;
	private NodeInfo HadoopUINode=null;
	private EnvHintCollection Hints=new EnvHintCollection();
	
	public WorkflowHadoopAdaptor()
	{
		this.OutputResources=new HashSet<IOutputResource>();
	}

	public void SetAdaptorResources(IAdaptorResources Resources) throws WorkflowValidationException
	{
		if(!(Resources instanceof AdaptorHadoopResources)) throw new WorkflowValidationException("Invalid adaptor resources provided");
		this.Resources=(AdaptorHadoopResources)Resources;
		this.Resources.Validate();
	}

	public void CreatePlan() throws WorkflowValidationException, WorkflowSerializationException, WorkflowInternalErrorException, WorkflowEnvironmentException
	{
		if(this.Resources==null) throw new WorkflowValidationException("No resources specified");
		this.ConstructEnvironmentHints();
		this.ConstructWorkflow();
		this.ExcludeOutputResourcesCleanUp();
	}
	
	public void SetExecutionId(String executionId)
	{
		this.ExecutionId = executionId;
	}
	
	private void ExcludeOutputResourcesCleanUp()
	{
		for(IOutputResource res : this.OutputResources)
		{
			if(!(res instanceof OutputHadoopResource)) continue;
			this.Plan.CleanUpSSExclude.Add(((OutputHadoopResource)res).VariableID);
		}
	}
	
	private void ConstructEnvironmentHints()
	{
		AttachedHadoopResource att = this.Resources.GetScopeResource();
		if(att!=null)
		{
			this.Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(att.Value)));
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
		try
		{
			Set<AttachedHadoopResource.ResourceType> ResourcesToStore=new HashSet<AttachedHadoopResource.ResourceType>();
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.Archive);
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.Configuration);
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.File);
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.Jar);
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.Lib);
			ResourcesToStore.add(AttachedHadoopResource.ResourceType.Input);
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
		BoundaryPlanElement  bound=this.ConstructBoundaryElement();
		this.Plan.Root=bound;
		bound.Root=new SequencePlanElement();
		
//		if(!this.HadoopUINode.TypeSpecificExtensions.containsKey(ExtensionPair.HADOOP_LOCATION)) throw new WorkflowEnvironmentException("Selected Hadoop UI node does not expose the needed environment extensions");
		
		SequencePlanElement seqCheck=this.CheckIfInputOutputDirectoriesExist(this.Resources.GetInputBaseDirs(),this.Resources.GetOutputBaseDirs());
		if(seqCheck!=null)((SequencePlanElement)bound.Root).ElementCollection.add(seqCheck);

		Set<AttachedHadoopResource> atts=new HashSet<AttachedHadoopResource>();
		if(this.Resources.GetArchiveResources().size()>0) atts.addAll(this.Resources.GetArchiveResources());
		if(this.Resources.GetConfigurationResource()!=null) atts.add(this.Resources.GetConfigurationResource());
		if(this.Resources.GetFileResources().size()>0) atts.addAll(this.Resources.GetFileResources());
		if(this.Resources.GetJarResource()!=null) atts.add(this.Resources.GetJarResource());
		if(this.Resources.GetLibResources().size()>0) atts.addAll(this.Resources.GetLibResources());
		for(AttachedHadoopResource att : atts) ((SequencePlanElement)bound.Root).ElementCollection.add(this.CreateRetrieveFileElement(att));
		SequencePlanElement seq=this.CreateRetrieveHDFSFileElement(this.Resources.GetInputResources(),this.Resources.GetInputBaseDirs());
		if(seq!=null) ((SequencePlanElement)bound.Root).ElementCollection.add(seq);
		String stdErrFile=UUID.randomUUID().toString();
		String stdOutFile=UUID.randomUUID().toString();
		TryCatchFinallyPlanElement tcf=new TryCatchFinallyPlanElement();
		((SequencePlanElement)bound.Root).ElementCollection.add(tcf);
		SequencePlanElement seqTry=new SequencePlanElement();
		SequencePlanElement seqFinally=new SequencePlanElement();
		tcf.TryFlow=seqTry;
		tcf.FinallyFlow=seqFinally;
		Set<HadoopInOutDirectoryInfo> outputBaseDirs = this.Resources.GetOutputBaseDirs();
		try
		{
			HadoopInOutDirectoryInfo stdOutInfo = null;
			HadoopInOutDirectoryInfo stdErrInfo = null;
			for(HadoopInOutDirectoryInfo innfo : outputBaseDirs)
			{
				if(innfo.Directory.equals(OutputType.StdOut))
					stdOutInfo = innfo;
				if(innfo.Directory.equals(OutputType.StdErr))
					stdErrInfo = innfo;
			}
			seqTry.ElementCollection.add(this.ChockError(this.SubmitAndRetrieve(this.SubmitJob(stdErrFile, stdOutFile),this.CreateStoreFileElementToStorage(stdOutFile,OutputHadoopResource.OutputType.StdOut, stdOutInfo),this.CreateStoreFileElementToStorage(stdErrFile,OutputHadoopResource.OutputType.StdErr, stdErrInfo))));
		} catch (ExecutionValidationException e)
		{
			throw new WorkflowEnvironmentException("Could not construct workflow", e);
		}
		for(HadoopInOutDirectoryInfo innfo : this.Resources.GetInputBaseDirs()) if(innfo.CleanUp && innfo.Directory!=null) seqFinally.ElementCollection.add(this.CreateCleanUpDirectoryElementFromHDFS(innfo.Directory));
		if(this.Resources.GetOutputResources().size()>0)
		{
			TryCatchFinallyPlanElement seqCopyOutputs=this.ChockError(this.CreateCopyDirectoryElementFromHDFS(this.Resources.GetOutputBaseDirs()));
			if(seqCopyOutputs.TryFlow==null || ((SequencePlanElement)seqCopyOutputs.TryFlow).ElementCollection.size()==0) throw new WorkflowValidationException("Did not find output directories to retrieve although they are expected");
			seqTry.ElementCollection.add(seqCopyOutputs);
			for(HadoopInOutDirectoryInfo outnfo : outputBaseDirs) if(outnfo.CleanUp) seqFinally.ElementCollection.add(this.ChockError(this.CreateCleanUpDirectoryElementFromHDFS(outnfo.Directory)));
			for(HadoopInOutDirectoryInfo outnfo : outputBaseDirs)
			{
				String archiveName=UUID.randomUUID().toString()+".tar.gz";
				seqTry.ElementCollection.add(this.ChockError(this.CreateOutputArchive(outnfo.Directory,archiveName)));
				try
				{
					seqTry.ElementCollection.add(this.ChockError(this.CreateStoreFileElementToStorage(archiveName,OutputHadoopResource.OutputType.OutputArchive, outnfo)));
				} catch (ExecutionValidationException e)
				{
					throw new WorkflowEnvironmentException("Could not construct workflow", e);
				}
			}
		}
		if(seqFinally.ElementCollection.size()==0) tcf.FinallyFlow=null;
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
	
	private SequencePlanElement CheckIfInputOutputDirectoriesExist(Set<HadoopInOutDirectoryInfo> inputBaseDirs, Set<HadoopInOutDirectoryInfo> outputBaseDirs) throws WorkflowValidationException
	{
		try
		{
			SequencePlanElement seq=new SequencePlanElement();
	
			if(inputBaseDirs.size()>0)
			{
				for(HadoopInOutDirectoryInfo innfo : inputBaseDirs)
				{
					ShellPlanElement subm=new ShellPlanElement();
					String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
					if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
					subm.Command=hadoop_location_string+"/bin/hadoop";
					subm.SetName("hadoop dfs input exists");
					subm.Environment.clear();
					subm.Triggers.clear();
					subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Input directory "+innfo.Directory+" already exists",MapType.Equal));
					subm.StdErrIsFile=false;
					subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
					subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
					subm.StdInIsFile=false;
					subm.StdInParameter=null;
					subm.StdOutIsFile=false;
					subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-ls",this.Plan)));
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(innfo.Directory,this.Plan)));
					seq.ElementCollection.add(subm);
				}
			}
			if(outputBaseDirs.size()>0)
			{
				for(HadoopInOutDirectoryInfo innfo : outputBaseDirs)
				{
					ShellPlanElement subm=new ShellPlanElement();
					String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
					if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
					subm.Command=hadoop_location_string+"/bin/hadoop";
					subm.SetName("hadoop dfs output exists");
					subm.Environment.clear();
					subm.Triggers.clear();
					subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Output "+innfo.Directory+" already exists",MapType.Equal));
					subm.StdErrIsFile=false;
					subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
					subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
					subm.StdInIsFile=false;
					subm.StdInParameter=null;
					subm.StdOutIsFile=false;
					subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-ls",this.Plan)));
					subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(innfo.Directory,this.Plan)));
					seq.ElementCollection.add(subm);
				}
			}
			if(seq.ElementCollection.size()==0) return null;
			return seq;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct check for existing input and output",ex);
		}
	}
	
	private TryCatchFinallyPlanElement SubmitAndRetrieve(ShellPlanElement submit,SequencePlanElement retrieveStdOut, SequencePlanElement retrieveStdErr)
	{
		TryCatchFinallyPlanElement tcf=new TryCatchFinallyPlanElement();
		tcf.TryFlow=submit;
		tcf.FinallyFlow=new SequencePlanElement();
		((SequencePlanElement)tcf.FinallyFlow).ElementCollection.add(retrieveStdErr);
		((SequencePlanElement)tcf.FinallyFlow).ElementCollection.add(retrieveStdOut);
		return tcf;
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
		try
		{
			this.HadoopUINode=InformationSystem.GetMatchingNode(null, "hadoop.gateway == true", Hints);
			if(this.HadoopUINode==null) throw new WorkflowEnvironmentException("Could not find appropriate hadoop UI node to host execution");
			logger.info("Selected Execution Engine: " + this.HadoopUINode.getExtension("hostname") + ":" + this.HadoopUINode.getExtension("pe2ng.port"));
		}catch(Exception ex)
		{
			throw new WorkflowEnvironmentException("Could not retrieve environment information from Information System", ex);
		}
		BoundaryConfig Config=new BoundaryConfig();
		Config.HostName=this.HadoopUINode.getExtension("hostname");
		Config.Port=Integer.parseInt(this.HadoopUINode.getExtension("pe2ng.port"));
		Config.NozzleConfig=new TCPServerNozzleConfig(false, 0);
		return Config;
	}

	private IPlanElement CreateRetrieveFileElement(AttachedHadoopResource attachment) throws WorkflowValidationException
	{
		if(attachment.IsHDFSPresent) return this.CreateRetrieveFileElementFromHDFS(attachment);
		else return this.CreateRetrieveFileElementFromStorage(attachment);
	}

	private SequencePlanElement CreateRetrieveHDFSFileElement(List<AttachedHadoopResource> attachments,Set<HadoopInOutDirectoryInfo> inputBaseDirs) throws WorkflowValidationException
	{
		if(attachments.size()==0) return null;
		SequencePlanElement seq=new SequencePlanElement();
		for(AttachedHadoopResource attachment : attachments)
		{
			seq.ElementCollection.add(this.CreateRetrieveFileElementFromStorage(attachment));
		}
		for(HadoopInOutDirectoryInfo innfo : inputBaseDirs)
		{
			if(innfo.Directory==null) throw new WorkflowValidationException("Input file must have exactly one level higher parent directory");
			seq.ElementCollection.add(this.CreateMoveFileElementToHDFS(innfo.Directory));
		}
		return seq;
	}
	
	private SequencePlanElement CreateOutputArchive(String dirName, String archiveName) throws WorkflowValidationException
	{
		try
		{
			String localDirName=dirName;
			if(localDirName.startsWith("/"))localDirName=localDirName.substring(1);
			
			SequencePlanElement seq=new SequencePlanElement();
			
			ShellPlanElement lschck=new ShellPlanElement();
			lschck.Command="/bin/ls";
			lschck.SetName("local directory exists");
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
			lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(dirName,this.Plan)));
			seq.ElementCollection.add(lschck);
			
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command="/bin/tar";
			subm.SetName("create output archive for "+localDirName);
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
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(localDirName,this.Plan)));
			
			ConditionalPlanElement condElem=new ConditionalPlanElement();
			condElem.IfFlow=new ConditionalFlow();
			condElem.IfFlow.Root=subm;
			condElem.IfFlow.Condition=new ConditionTree();
			condElem.IfFlow.Condition.Root=new ConditionTreeLeaf();
			((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition=new DecimalRangePlanCondition();
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).CurrentValueParameter=((SimpleInOutParameter)lschck.StdExitValueParameter);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).LeftBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RightBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeStartParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeEndParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			
			seq.ElementCollection.add(condElem);

			return seq;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}
	
	private SequencePlanElement CreateCleanUpDirectoryElementFromHDFS(String dirName) throws WorkflowValidationException
	{
		try
		{
			String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
			if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
			
			SequencePlanElement seq=new SequencePlanElement();
			
			ShellPlanElement lschck=new ShellPlanElement();
			lschck.Command=hadoop_location_string+"/bin/hadoop";
			lschck.SetName("hadoop dfs directory exists");
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
			lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
			lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-ls",this.Plan)));
			lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(dirName,this.Plan)));
			seq.ElementCollection.add(lschck);
			
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command=hadoop_location_string+"/bin/hadoop";
			subm.SetName("hadoop dfs remove directory");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not cleanup directory from hdfs",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-rmr",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(dirName,this.Plan)));
			
			ConditionalPlanElement condElem=new ConditionalPlanElement();
			condElem.IfFlow=new ConditionalFlow();
			condElem.IfFlow.Root=subm;
			condElem.IfFlow.Condition=new ConditionTree();
			condElem.IfFlow.Condition.Root=new ConditionTreeLeaf();
			((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition=new DecimalRangePlanCondition();
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).CurrentValueParameter=((SimpleInOutParameter)lschck.StdExitValueParameter);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).LeftBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RightBorderInclusive=true;
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeStartParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeEndParameter=AdaptorUtils.GetInParameter("0", this.Plan);
			
			seq.ElementCollection.add(condElem);
			return seq;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}

	private SequencePlanElement CreateCopyDirectoryElementFromHDFS(Set<HadoopInOutDirectoryInfo> dirNames) throws WorkflowValidationException
	{
		try
		{
			String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
			if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
			
			SequencePlanElement seq=new SequencePlanElement();
			for(HadoopInOutDirectoryInfo outnfo : dirNames)
			{
				String localOutDir=outnfo.Directory;
				if(localOutDir.startsWith("/")) localOutDir=localOutDir.substring(1);
				
				SequencePlanElement seqInternal=new SequencePlanElement();
				
				ShellPlanElement lschck=new ShellPlanElement();
				lschck.Command=hadoop_location_string+"/bin/hadoop";
				lschck.SetName("hadoop dfs directory exists");
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
				lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
				lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-ls",this.Plan)));
				lschck.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(localOutDir,this.Plan)));
				seqInternal.ElementCollection.add(lschck);
				
				ShellPlanElement subm=new ShellPlanElement();
				subm.Command=hadoop_location_string+"/bin/hadoop";
				subm.SetName("hadoop dfs copy to local dir "+outnfo.Directory);
				subm.Environment.clear();
				subm.Triggers.clear();
				subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not copy output files from hdfs",MapType.NotEqual));
				subm.StdErrIsFile=false;
				subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
				subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
				subm.StdInIsFile=false;
				subm.StdInParameter=null;
				subm.StdOutIsFile=false;
				subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-copyToLocal",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(outnfo.Directory,this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(localOutDir,this.Plan)));
				
				ConditionalPlanElement condElem=new ConditionalPlanElement();
				condElem.IfFlow=new ConditionalFlow();
				condElem.IfFlow.Root=subm;
				condElem.IfFlow.Condition=new ConditionTree();
				condElem.IfFlow.Condition.Root=new ConditionTreeLeaf();
				((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition=new DecimalRangePlanCondition();
				((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).CurrentValueParameter=((SimpleInOutParameter)lschck.StdExitValueParameter);
				((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).LeftBorderInclusive=true;
				((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RightBorderInclusive=true;
				((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeStartParameter=AdaptorUtils.GetInParameter("0", this.Plan);
				((DecimalRangePlanCondition)((ConditionTreeLeaf)condElem.IfFlow.Condition.Root).Condition).RangeEndParameter=AdaptorUtils.GetInParameter("0", this.Plan);
				
				seqInternal.ElementCollection.add(condElem);

				seq.ElementCollection.add(seqInternal);
			}
			if(seq.ElementCollection.size()==0) return null;
			return seq;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}

	private ShellPlanElement CreateMoveFileElementToHDFS(String dirName) throws WorkflowValidationException
	{
		try
		{
			String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
			if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command=hadoop_location_string+"/bin/hadoop";
			subm.SetName("hadoop dfs move from local");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not move input files to hdfs",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-moveFromLocal",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(dirName,this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(dirName,this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}

	private ShellPlanElement CreateRetrieveFileElementFromHDFS(AttachedHadoopResource attachment) throws WorkflowValidationException
	{
		try
		{
			String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
			if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command=hadoop_location_string+"/bin/hadoop";
			subm.SetName("hadoop dfs copy to local");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not retrieve stored file from hdfs",MapType.NotEqual));
			subm.StdErrIsFile=false;
			subm.StdErrParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=false;
			subm.StdOutParameter=AdaptorUtils.GetInOutPrameter(this.Plan);
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("dfs",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-copyToLocal",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(attachment.Value,this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(attachment.Key,this.Plan)));
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct retrieve file script",ex);
		}
	}
	
	private ShellPlanElement SubmitJob(String stdErrFile, String stdOutFile) throws WorkflowValidationException
	{
		try
		{
			String hadoop_location_string=this.HadoopUINode.getExtension("hadoop.hadoop_location");
			if(hadoop_location_string==null || hadoop_location_string.trim().length()==0) throw new  WorkflowValidationException("needed property hadoop.hadoop_location not set in hadoop.gateway node");
			ShellPlanElement subm=new ShellPlanElement();
			subm.Command=hadoop_location_string+"/bin/hadoop";
			subm.SetName("hadoop jar submit");
			subm.Environment.clear();
			subm.Triggers.clear();
			subm.ExitCodeErrors.add(AdaptorUtils.GetExitCodeMapping(0, "Could not submit job",MapType.NotEqual));
			subm.StdErrIsFile=true;
			subm.StdErrParameter=AdaptorUtils.GetInOutParameterWithValue(stdErrFile,this.Plan);
			subm.StdErrOnlineFilter=new ParameterExternalFilter();
			((ParameterExternalFilter)subm.StdErrOnlineFilter).ExternalFilter=new HadoopVerboseProgressOutputFilter();
			((HadoopVerboseProgressOutputFilter)((ParameterExternalFilter)subm.StdErrOnlineFilter).ExternalFilter).PlanNodeID=subm.GetID();
			subm.StdExitValueParameter=AdaptorUtils.GetOutPrameter(this.Plan);
			subm.StdInIsFile=false;
			subm.StdInParameter=null;
			subm.StdOutIsFile=true;
			subm.StdOutParameter=AdaptorUtils.GetInOutParameterWithValue(stdOutFile,this.Plan);

			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("jar",this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetJarResource().Key,this.Plan)));
			subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetMainClassResource().Key,this.Plan)));
			
			if(this.Resources.GetConfigurationResource()!=null)
			{
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-conf",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(this.Resources.GetConfigurationResource().Value,this.Plan)));
			}
			for(AttachedHadoopResource att : this.Resources.GetPropertyResources())
			{
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-D"+att.Value,this.Plan)));
			}
			if(this.Resources.GetFileResources().size()>0)
			{
				StringBuilder buf=new StringBuilder();
				for(AttachedHadoopResource att : this.Resources.GetFileResources()) buf.append(att.Key+",");
				if(buf.length()>0) buf.deleteCharAt(buf.length()-1);
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-files",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(buf.toString(),this.Plan)));
			}
			if(this.Resources.GetLibResources().size()>0)
			{
				StringBuilder buf=new StringBuilder();
				for(AttachedHadoopResource att : this.Resources.GetLibResources()) buf.append(att.Key+",");
				if(buf.length()>0) buf.deleteCharAt(buf.length()-1);
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-libjars",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(buf.toString(),this.Plan)));
			}
			if(this.Resources.GetArchiveResources().size()>0)
			{
				StringBuilder buf=new StringBuilder();
				for(AttachedHadoopResource att : this.Resources.GetArchiveResources()) buf.append(att.Key+",");
				if(buf.length()>0) buf.deleteCharAt(buf.length()-1);
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter("-archives",this.Plan)));
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(buf.toString(),this.Plan)));
			}
			for(AttachedHadoopResource att : this.Resources.GetArgumentResources())
			{
				subm.ArgumentParameters.add(new AttributedInputParameter(AdaptorUtils.GetInParameter(att.Value,this.Plan)));
			}
			return subm;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowValidationException("Could not construct submit job script",ex);
		}
	}
	
	private SequencePlanElement CreateStoreFileElementToStorage(String filename,OutputHadoopResource.OutputType TypeOfOutput,HadoopInOutDirectoryInfo dirInfo) throws WorkflowValidationException, ExecutionValidationException
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
		if(dirInfo != null && dirInfo.OutputStoreMode == OutStoreMode.Url)
		{
			boolean slashFound = dirInfo.OutputStoreLocation.charAt(dirInfo.OutputStoreLocation.length()-1) == '/';
			ftr.OutputStoreMode = StoreMode.Url;
			//ftr.StoreUrlLocation = dirInfo.OutputStoreLocation + "/" + (this.ExecutionId != null ? this.ExecutionId + "/" : "") + filename;
			ftr.StoreUrlLocation = dirInfo.OutputStoreLocation + (slashFound ? "" : "/") + (this.ExecutionId != null ? this.ExecutionId + "." : "" ) + filename;
			ftr.accessInfo.port = dirInfo.accessInfo.port;
			ftr.accessInfo.userId = dirInfo.accessInfo.userId;
			ftr.accessInfo.password = dirInfo.accessInfo.password;
		}
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
		OutputHadoopResource out=new OutputHadoopResource();
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
	}
	
	private FileTransferPlanElement CreateRetrieveFileElementFromStorage(AttachedHadoopResource attachment) throws WorkflowValidationException
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
