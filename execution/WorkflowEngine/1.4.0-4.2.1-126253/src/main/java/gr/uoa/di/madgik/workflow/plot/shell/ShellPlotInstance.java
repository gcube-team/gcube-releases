package gr.uoa.di.madgik.workflow.plot.shell;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.ShellInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotShellParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.ShellPlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.PlotErrorMapping;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExceptionExitCodeMaping;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.AttributedInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.PlotInstanceBase;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShellPlotInstance extends PlotInstanceBase
{
	private ShellInvocableProfileInfo InvocableInfo=null;
	private ShellPlotInfo PlotInfo=null;
	private ShellPlotResourceParameterInCollection PlotInputParameters=null;
	private ShellPlotResourceParameterOutCollection PlotOutputParameters=null;
	private ShellPlanElement Element=null;
	
	public IPlanElement GetElement()
	{
		return this.Element;
	}

	public InvocableProfileInfo GetInvocableProfile()
	{
		return this.InvocableInfo;
	}

	public void SetInvocableProfile(InvocableProfileInfo invocable) throws WorkflowValidationException
	{
		if(!(invocable instanceof ShellInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.InvocableInfo=(ShellInvocableProfileInfo)invocable;
		this.SetName(this.InvocableInfo.ExecutableName);
	}

	public InvocablePlotInfo GetPlotProfile()
	{
		return this.PlotInfo;
	}

	public void SetPlotProfile(InvocablePlotInfo plot) throws WorkflowValidationException
	{
		if(!(plot instanceof ShellPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInfo=(ShellPlotInfo)plot;
	}

	public IPlotResourceInCollection GetInputParameterCollection()
	{
		return this.PlotInputParameters;
	}

	public void SetInputParameterCollection(IPlotResourceInCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof ShellPlotResourceParameterInCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInputParameters=(ShellPlotResourceParameterInCollection)parameters;
	}

	public IPlotResourceOutCollection GetOutputParameterCollection()
	{
		return this.PlotOutputParameters;
	}

	public void SetOutputParameterCollection(IPlotResourceOutCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof ShellPlotResourceParameterOutCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotOutputParameters=(ShellPlotResourceParameterOutCollection)parameters;
	}

	public void Validate() throws WorkflowValidationException
	{
		if(this.InvocableInfo==null || this.PlotInfo==null) throw new WorkflowValidationException("Plot and/or invocable profile not provided");
		if(this.PlotInputParameters==null) throw new WorkflowValidationException("Plot input parameters not provided");
		if(this.PlotOutputParameters==null) throw new WorkflowValidationException("Plot output parameters not provided");
		if(this.GetLocalEnvironmentFilesParameterCollection()==null) throw new WorkflowValidationException("Plot output parameters not provided");
		this.PlotInputParameters.SetInvocableInfo(InvocableInfo);
		this.PlotInputParameters.SetPlotInfo(PlotInfo);
		this.PlotInputParameters.Validate();
		this.PlotOutputParameters.SetInvocableInfo(InvocableInfo);
		this.PlotOutputParameters.SetPlotInfo(PlotInfo);
		this.PlotOutputParameters.Validate();
		this.GetLocalEnvironmentFilesParameterCollection().SetInvocableInfo(InvocableInfo);
		this.GetLocalEnvironmentFilesParameterCollection().SetPlotInfo(PlotInfo);
		this.GetLocalEnvironmentFilesParameterCollection().Validate();
	}

	public void Process() throws WorkflowProcessException, WorkflowEnvironmentException
	{
		this.Element=new ShellPlanElement();
		this.Element.SetName(this.GetName());
		this.Element.Command=this.InvocableInfo.ExecutableName;
		this.Element.Triggers=this.GetContingencyTriggers(this.PlotInfo);
		this.Element.ExitCodeErrors=this.GetExceptionExitCodeMapping();
		this.Element.Environment=this.GetEnvironmentVariables();
		this.Element.StdInParameter=this.GetStdInParameter();
		this.Element.StdInIsFile=this.GetIsStdInFile();
		this.Element.StdOutParameter=this.GetStdOutParameter();
		this.Element.StdOutIsFile=this.GetIsStdOutFile();
		this.Element.StdErrParameter=this.GetStdErrParameter();
		this.Element.StdErrIsFile=this.GetIsStdErrFile();
		this.Element.StdExitValueParameter=this.GetStdExitParameter();
		this.Element.ArgumentParameters=this.GetArgumentList();
		this.PopulatePrePostElements(this.PlotInfo);
	}
	
	private List<AttributedInputParameter> GetArgumentList() throws WorkflowProcessException
	{
		try
		{
			List<AttributedInputParameter> params=new ArrayList<AttributedInputParameter>();
			Collections.sort(this.PlotInfo.Parameters);
			for(PlotShellParameter par : this.PlotInfo.Parameters)
			{
				ShellPlotResourceParameter p=this.PlotInputParameters.GetParameter(par.ParameterName);
				if(!par.IsFixed) params.add(new AttributedInputParameter(p.Parameter,par.IsFile));
				else
				{
					NamedDataType ndt= this.ConstructNamedDataType(IDataType.DataTypes.String.toString(), true, par.FixedValue);
					params.add(new AttributedInputParameter(this.ConstructAndRegisterSimpleInputParameter(ndt),par.IsFile));
				}
			}
			return params;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowProcessException("Could not retreive argument",ex);
		}
	}
	
	private IInputParameter GetStdInParameter()
	{
		ShellPlotResourceStdIn stdin=this.PlotInputParameters.GetStdIn();
		if(stdin==null) return null;
		return stdin.Input;
	}
	
	private boolean GetIsStdInFile()
	{
		ShellPlotResourceStdIn stdin=this.PlotInputParameters.GetStdIn();
		if(stdin==null) return false;
		return stdin.IsFile;
	}
	
	private IInputOutputParameter GetStdOutParameter()
	{
		ShellPlotResourceStdOut stdout=this.PlotOutputParameters.GetStdOut();
		if(stdout==null) return null;
		return stdout.Output;
	}
	
	private boolean GetIsStdOutFile()
	{
		ShellPlotResourceStdOut stdout=this.PlotOutputParameters.GetStdOut();
		if(stdout==null) return false;
		return stdout.IsFile;
	}
	
	private IInputOutputParameter GetStdErrParameter()
	{
		ShellPlotResourceStdErr stderr=this.PlotOutputParameters.GetStdErr();
		if(stderr==null) return null;
		return stderr.Output;
	}
	
	private boolean GetIsStdErrFile()
	{
		ShellPlotResourceStdErr stderr=this.PlotOutputParameters.GetStdErr();
		if(stderr==null) return false;
		return stderr.IsFile;
	}
	
	private IOutputParameter GetStdExitParameter()
	{
		ShellPlotResourceStdExit stdexit=this.PlotOutputParameters.GetStdExit();
		if(stdexit==null) return null;
		return stdexit.Output;
	}
	
	private List<ExceptionExitCodeMaping> GetExceptionExitCodeMapping()
	{
		List<ExceptionExitCodeMaping> maps=new ArrayList<ExceptionExitCodeMaping>();
		for(PlotErrorMapping map : this.PlotInfo.ErrorMappings)
		{
			ExceptionExitCodeMaping m=new ExceptionExitCodeMaping();
			m.ErrorFullName=map.FullErrorName;
			m.ErrorSimpleName=map.SimpleErrorName;
			m.ExitCode=map.ExitCode;
			m.Message=map.Message;
			maps.add(m);
		}
		return maps;
	}
	
	private List<EnvironmentKeyValue> GetEnvironmentVariables()
	{
		List<EnvironmentKeyValue> vars=new ArrayList<EnvironmentKeyValue>();
		for(PlotLocalEnvironmentVariable var : this.PlotInfo.LocalEnvironment.Variables)
		{
			EnvironmentKeyValue v=new EnvironmentKeyValue();
			v.Key=var.Name;
			if(var.IsFixed) v.Value=var.Value;
			else v.Value=this.PlotInputParameters.GetEnvironmentVariable(var.Name).Value;
			vars.add(v);
		}
		return vars;
	}
}
