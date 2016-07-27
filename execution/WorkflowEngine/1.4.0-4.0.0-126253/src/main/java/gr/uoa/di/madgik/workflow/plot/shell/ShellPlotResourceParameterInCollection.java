package gr.uoa.di.madgik.workflow.plot.shell;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.ShellInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotShellParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.ShellPlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import java.util.HashMap;
import java.util.Map;

public class ShellPlotResourceParameterInCollection implements IPlotResourceInCollection
{
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	public Map<String, ShellPlotResourceEnvironmentVariable> EnvVariables=new HashMap<String, ShellPlotResourceEnvironmentVariable>();
	public Map<String,ShellPlotResourceParameter> Parameters=new HashMap<String, ShellPlotResourceParameter>();
	public ShellPlotResourceStdIn StdInParameter=null;

	public void SetPlotInfo(InvocablePlotInfo plotInfo)
	{
		this.plotInfo=plotInfo;
	}

	public void SetInvocableInfo(InvocableProfileInfo invocableInfo)
	{
		this.invocableInfo=invocableInfo;
	}

	public void Add(IPlotResource param) throws WorkflowValidationException
	{
		if(param instanceof ShellPlotResourceEnvironmentVariable) this.EnvVariables.put(((ShellPlotResourceEnvironmentVariable)param).Name, (ShellPlotResourceEnvironmentVariable)param);
		else if (param instanceof ShellPlotResourceStdIn) this.StdInParameter=(ShellPlotResourceStdIn)param;
		else if (param instanceof ShellPlotResourceParameter) this.Parameters.put(((ShellPlotResourceParameter)param).Name, (ShellPlotResourceParameter)param);
		else throw new WorkflowValidationException("unrecognized type of resource provided");
	}
	
	public ShellPlotResourceEnvironmentVariable GetEnvironmentVariable(String name)
	{
		return this.EnvVariables.get(name);
	}
	
	public ShellPlotResourceStdIn GetStdIn()
	{
		return this.StdInParameter;
	}
	
	public ShellPlotResourceParameter GetParameter(String Name)
	{
		return this.Parameters.get(Name);
	}

	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof ShellPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof ShellInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(((ShellPlotInfo)this.plotInfo).UseStdIn && (this.StdInParameter==null || this.StdInParameter.Input==null)) throw new WorkflowValidationException("Needed input parameter not set");
		for(PlotLocalEnvironmentVariable var : ((ShellPlotInfo)this.plotInfo).LocalEnvironment.Variables)
		{
			if(!var.IsFixed)
			{
				if(this.GetEnvironmentVariable(var.Name)==null) throw new WorkflowValidationException("Needed environment variable not provided");
				if(this.GetEnvironmentVariable(var.Name).Value==null) throw new WorkflowValidationException("Needed environment variable not provided");
			}
		}
		for(PlotShellParameter param : ((ShellPlotInfo)this.plotInfo).Parameters)
		{
			if(!param.IsFixed)
			{
				if(this.GetParameter(param.ParameterName)==null) throw new WorkflowValidationException("Needed parameter not provided");
				if(this.GetParameter(param.ParameterName).Parameter==null) throw new WorkflowValidationException("Needed parameter not provided");
			}
		}
	}

}
