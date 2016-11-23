package gr.uoa.di.madgik.workflow.plot;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.PojoInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.ShellInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSInvocableProfileInfo;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.HostInfo;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import gr.uoa.di.madgik.workflow.plot.commons.PlotResourceEnvironmentFileCollection;
import gr.uoa.di.madgik.workflow.plot.pojo.PojoPlotInstance;
import gr.uoa.di.madgik.workflow.plot.shell.ShellPlotInstance;
import gr.uoa.di.madgik.workflow.plot.ws.WSPlotInstance;
import java.util.List;
import java.util.Set;

public class SketchPlot
{
	public PlotSelectionCriteria SelectionCriteria=null;
	public String Name=null;
	public List<ContingencyTrigger> Contingency=null;

	private InvocablePlotInfo PlotInfo=null;
	private InvocableProfileInfo InvocableInfo=null;
	private IPlotInstance PlotInstance=null;	private IPlotResourceInCollection InResources=null;
	private IPlotResourceOutCollection OutResources=null;
	private PlotResourceEnvironmentFileCollection FileResources=null;
	private VariableCollection Variables=null;
	
	public void SetVariableCollection(VariableCollection Variables)
	{
		this.Variables=Variables;
	}
	
	public void SetInResources(IPlotResourceInCollection Resources)
	{
		this.InResources=Resources;
	}
	
	public void SetOutResources(IPlotResourceOutCollection Resources)
	{
		this.OutResources=Resources;
	}
	
	public void SetFileResources(PlotResourceEnvironmentFileCollection Resources)
	{
		this.FileResources=Resources;
	}
	
	public void Sketch(EnvHintCollection Hints) throws WorkflowValidationException, WorkflowEnvironmentException, WorkflowProcessException
	{
		this.Validate();
		this.SelectPlot(Hints);
		this.PlotInstance=this.GetPlotInstance();
		this.PopulatePlotInstance(Hints);
		this.PlotInstance.Validate();
		this.PlotInstance.Process();
		for(NamedDataType ndt : this.PlotInstance.GetAdditionalVariables()) this.Variables.Add(ndt);
	}
	
	public IPlanElement GetSketchedElement() throws WorkflowProcessException
	{
		if(this.PlotInstance==null) throw new WorkflowProcessException("Element not sketched yet");
		IPlanElement element=null;

		if(this.PlotInstance.GetPreElement().size()==0 && this.PlotInstance.GetPostElement().size()==0)
		{
			element = this.PlotInstance.GetElement();
		}
		else
		{
			SequencePlanElement seq=new SequencePlanElement();
			seq.SetName(this.PlotInstance.GetElement().GetName()+" sequence");
			for(IPlanElement elem : this.PlotInstance.GetPreElement()) seq.ElementCollection.add(elem);
			seq.ElementCollection.add(this.PlotInstance.GetElement());
			for(IPlanElement elem : this.PlotInstance.GetPostElement()) seq.ElementCollection.add(elem);
			element=seq;
		}
		return element;
	}
	
	public Set<String> GetCleanupFiles() throws WorkflowProcessException
	{
		if(this.PlotInstance==null) throw new WorkflowProcessException("Element not sketched yet");
		return this.PlotInstance.GetCleanupLocalFiles();
	}
	
	private void PopulatePlotInstance(EnvHintCollection Hints) throws WorkflowValidationException, WorkflowEnvironmentException
	{
		if(this.Name!=null) this.PlotInstance.OverrideName(this.Name);
		if(this.Contingency!=null) this.PlotInstance.OverrideContingencyTriggers(this.Contingency);
		this.PlotInstance.SetPlotProfile(this.PlotInfo);
		this.PlotInstance.SetInvocableProfile(this.InvocableInfo);
		this.PlotInstance.SetHostInfo(this.GetHostInfo(Hints));
		this.PlotInstance.SetLocalEnvironmentFilesParameterCollection(this.FileResources);
		this.PlotInstance.SetInputParameterCollection(this.InResources);
		this.PlotInstance.SetOutputParameterCollection(this.OutResources);
	}
	
	private HostInfo GetHostInfo(EnvHintCollection Hints) throws WorkflowEnvironmentException
	{
//		try
//		{
//			Removed the respective method form the Information Provider
//			return new HostInfo(InformationSystem.GetNodeHostingInvocable(this.InvocableInfo.ID,Hints));
			return null;
//		}catch(EnvironmentInformationSystemException ex)
//		{
//			throw new WorkflowEnvironmentException("Could not select plot to use", ex);
//		}
	}
	
	private IPlotInstance GetPlotInstance() throws WorkflowValidationException
	{
		if(this.InvocableInfo instanceof ShellInvocableProfileInfo)
		{
			return new ShellPlotInstance();
		}
		else if (this.InvocableInfo instanceof PojoInvocableProfileInfo)
		{
			return new PojoPlotInstance();
		}
		else if (this.InvocableInfo instanceof WSInvocableProfileInfo)
		{
			return new WSPlotInstance();
		}
		else throw new WorkflowValidationException("Unrecognizable invocableprofile ");
	}
	
	private void Validate() throws WorkflowValidationException
	{
		if(this.SelectionCriteria==null)throw new WorkflowValidationException("No plot selection defined");
		this.SelectionCriteria.Validate();
		if(this.Variables==null) throw new WorkflowValidationException("Variables of plan are not set");
		if(this.InResources==null || this.OutResources==null || this.FileResources==null) throw new WorkflowValidationException("Resource of plot to use are not set");
	}
	
	private void SelectPlot(EnvHintCollection Hints) throws WorkflowEnvironmentException
	{
//		Removed the respective method form the Information Provider
//		try
//		{
//			this.PlotInfo=InformationSystem.GetPlotWithName(this.SelectionCriteria.PlotName,Hints);
//			if(this.PlotInfo==null) throw new WorkflowEnvironmentException("Could not find matching plot");
//			this.InvocableInfo=InformationSystem.GetInvocableProfile(this.PlotInfo.InvocabeProfileID,Hints);
//			if(this.InvocableInfo==null) throw new WorkflowEnvironmentException("Could not find matching invocable");
//		}catch(EnvironmentInformationSystemException ex)
//		{
//			throw new WorkflowEnvironmentException("Could not select plot to use", ex);
//		}
	}
}
