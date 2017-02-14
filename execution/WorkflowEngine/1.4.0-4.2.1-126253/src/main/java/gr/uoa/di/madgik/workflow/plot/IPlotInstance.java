package gr.uoa.di.madgik.workflow.plot;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase.ContextProxyType;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.HostInfo;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import gr.uoa.di.madgik.workflow.plot.commons.PlotResourceEnvironmentFileCollection;
import java.util.List;
import java.util.Set;

public interface IPlotInstance
{
	public void SetContextProxyType(ContextProxyType ProxyType);
	
	public ContextProxyType GetContextProxyType();
	
	public void SetHostInfo(HostInfo hostInfo) throws WorkflowValidationException;
	
	public HostInfo GetHostInfo();
	
	public void OverrideName(String Name);
	
	public void OverrideContingencyTriggers(List<ContingencyTrigger> triggers);
	
	public void SetPlotProfile(InvocablePlotInfo plot) throws WorkflowValidationException;
	
	public InvocablePlotInfo GetPlotProfile();
	
	public void SetInvocableProfile(InvocableProfileInfo invocable) throws WorkflowValidationException;
	
	public InvocableProfileInfo GetInvocableProfile();

	public void SetLocalEnvironmentFilesParameterCollection(PlotResourceEnvironmentFileCollection parameters) throws WorkflowValidationException;
	
	public PlotResourceEnvironmentFileCollection GetLocalEnvironmentFilesParameterCollection();

	public void SetInputParameterCollection(IPlotResourceInCollection parameters) throws WorkflowValidationException;
	
	public IPlotResourceInCollection GetInputParameterCollection();

	public void SetOutputParameterCollection(IPlotResourceOutCollection parameters) throws WorkflowValidationException;
	
	public IPlotResourceOutCollection GetOutputParameterCollection();
	
	public void Validate() throws WorkflowValidationException;
	
	public Set<NamedDataType> GetAdditionalVariables();
	
	public void Process() throws WorkflowProcessException,WorkflowEnvironmentException;
	
	public Set<String> GetCleanupLocalFiles();
	
	public List<IPlanElement> GetPreElement();
	
	public IPlanElement GetElement();
	
	public List<IPlanElement> GetPostElement();
	
}
