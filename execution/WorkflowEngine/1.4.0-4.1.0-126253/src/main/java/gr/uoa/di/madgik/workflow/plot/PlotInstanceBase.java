package gr.uoa.di.madgik.workflow.plot;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingency;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyReactionPick;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyReactionRetry;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement.TransferDirection;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionNone;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionPick;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyReactionRetry;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase.ContextProxyType;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.SimpleInParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.HostInfo;
import gr.uoa.di.madgik.workflow.plot.commons.PlotResourceEnvironmentFile;
import gr.uoa.di.madgik.workflow.plot.commons.PlotResourceEnvironmentFileCollection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class PlotInstanceBase implements IPlotInstance
{
	private String Name=null;
	private Set<NamedDataType> AdditionalVariables=new HashSet<NamedDataType>();
	private List<ContingencyTrigger> Triggers;
	private List<IPlanElement> PreElements=new ArrayList<IPlanElement>();
	private List<IPlanElement> PostElements=new ArrayList<IPlanElement>();
	private PlotResourceEnvironmentFileCollection PlotLocalEnvironmentFilesParameters=null;
	private HostInfo hostInfo=null;
	private Set<String> CleanupFiles=new HashSet<String>();
	private ContextProxyType ProxyTypeInContext=ContextProxyType.TCP;

	public void SetContextProxyType(ContextProxyType ProxyType)
	{
		this.ProxyTypeInContext=ProxyType;
	}
	
	public ContextProxyType GetContextProxyType()
	{
		return this.ProxyTypeInContext;
	}
	
	public void SetHostInfo(HostInfo hostInfo) throws WorkflowValidationException
	{
		this.hostInfo=hostInfo;
	}
	
	public HostInfo GetHostInfo()
	{
		return this.hostInfo;
	}
	
	protected void SetName(String Name)
	{
		this.Name=Name;
	}

	public void OverrideName(String Name)
	{
		this.Name=Name;
	}
	
	public String GetName()
	{
		return this.Name;
	}

	public Set<NamedDataType> GetAdditionalVariables()
	{
		return this.AdditionalVariables;
	}
	
	protected void AddAdditionalVariable(NamedDataType ndt)
	{
		this.AdditionalVariables.add(ndt);
	}
	
	public PlotResourceEnvironmentFileCollection GetLocalEnvironmentFilesParameterCollection()
	{
		return this.PlotLocalEnvironmentFilesParameters;
	}

	public void SetLocalEnvironmentFilesParameterCollection(PlotResourceEnvironmentFileCollection parameters) throws WorkflowValidationException
	{
		this.PlotLocalEnvironmentFilesParameters=parameters;
	}

	public void OverrideContingencyTriggers(List<ContingencyTrigger> triggers)
	{
		this.Triggers=triggers;
	}
	
	protected List<ContingencyTrigger> GetContingencyTriggers(InvocablePlotInfo PlotInfo) throws WorkflowEnvironmentException
	{
		if(this.Triggers!=null) return this.Triggers;
		List<ContingencyTrigger> triggers=new ArrayList<ContingencyTrigger>();
		for(InvocablePlotContingency cont : PlotInfo.Triggers)
		{
			ContingencyTrigger trigg=new ContingencyTrigger();
			trigg.IsFullNameOfError=cont.Trigger.IsFullName;
			trigg.TriggeringError=cont.Trigger.ErrorName;
			switch(cont.Reaction.GetReactionType())
			{
				case None:
				{
					trigg.Reaction=new ContingencyReactionNone();
					break;
				}
				case Retry:
				{
					trigg.Reaction=new ContingencyReactionRetry();
					((ContingencyReactionRetry)trigg.Reaction).NumberOfRetries=((InvocablePlotContingencyReactionRetry)cont.Reaction).NumberOfRetries;
					((ContingencyReactionRetry)trigg.Reaction).RetryInterval=((InvocablePlotContingencyReactionRetry)cont.Reaction).RetryInterval;
					break;
				}
				case Pick:
				{
					trigg.Reaction=new ContingencyReactionPick();
					((ContingencyReactionPick)trigg.Reaction).ExhaustPickList=((InvocablePlotContingencyReactionPick)cont.Reaction).ExhaustLists;
					((ContingencyReactionPick)trigg.Reaction).PickList=((InvocablePlotContingencyReactionPick)cont.Reaction).DefaultList;
					((ContingencyReactionPick)trigg.Reaction).RetrievePickList=((InvocablePlotContingencyReactionPick)cont.Reaction).Query;
					break;
				}
				default:
				{
					throw new WorkflowEnvironmentException("Unrecognized reaction type");
				}
			}
			triggers.add(trigg);
		}
		return triggers;
	}
	
	public List<IPlanElement> GetPreElement()
	{
		return this.PreElements;
	}
	
	public List<IPlanElement> GetPostElement()
	{
		return this.PostElements;
	}
	
	public Set<String> GetCleanupLocalFiles()
	{
		return this.CleanupFiles;
	}
	
	protected void PopulatePrePostElements(InvocablePlotInfo PlotInfo) throws WorkflowEnvironmentException, WorkflowProcessException
	{
		try
		{
			for(PlotLocalEnvironmentFile f : PlotInfo.LocalEnvironment.Files)
			{
				FileTransferPlanElement fe=new FileTransferPlanElement();
				switch(f.Direction)
				{
					case In:
					{
						fe.Direction=TransferDirection.Retrieve;
						NamedDataType ndt=this.ConstructNamedDataType(IDataType.DataTypes.String.toString(), true, f.Location);
						fe.MoveTo=this.ConstructAndRegisterSimpleInputParameter(ndt);
						this.PreElements.add(fe);
						break;
					}
					case Out:
					{
						fe.Direction=TransferDirection.Store;
						this.PostElements.add(fe);
						break;
					}
					default:
					{
						throw new WorkflowEnvironmentException("unrecognized direction found");
					}
				}
				fe.IsExecutable=f.IsExecutable;
				PlotResourceEnvironmentFile par = this.PlotLocalEnvironmentFilesParameters.Get(f.Name);
				fe.Input=par.InParameter;
				fe.Output=par.OutParameter;
				this.CleanupFiles.add(f.Location);
			}
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowProcessException("Could not retreive pre and or post element environment",ex);
		}
	}
	
	protected IInputParameter ConstructAndRegisterSimpleInputParameter(NamedDataType ndt)
	{
		this.AddAdditionalVariable(ndt);
		SimpleInParameter inParam=new SimpleInParameter();
		inParam.VariableName=ndt.Name;
		return inParam;
	}
	
	protected NamedDataType ConstructNamedDataType(String engineType,boolean isAvailable,String value) throws ExecutionValidationException
	{
		NamedDataType ndt=new NamedDataType();
		ndt.IsAvailable=isAvailable;
		ndt.Name=UUID.randomUUID().toString();
		ndt.Token=ndt.Name;
		IDataType.DataTypes dt=DataTypeUtils.GetDataTypeOfEngineType(engineType);
		ndt.Value=DataTypeUtils.GetDataType(dt);
		if(dt.equals(IDataType.DataTypes.Array))
		{
			((DataTypeArray)ndt.Value).SetArrayClassCode(engineType);
		}
		if(isAvailable)ndt.Value.SetValue(value);
		return ndt;
	}
}
