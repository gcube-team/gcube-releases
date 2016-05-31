package gr.uoa.di.madgik.workflow.plot.pojo;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.Method;
import gr.uoa.di.madgik.environment.is.elements.invocable.Parameter;
import gr.uoa.di.madgik.environment.is.elements.invocable.PojoInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.PojoPlotInfo;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.PojoPlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase.ContextProxyType;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleCall;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.PlotInstanceBase;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import java.util.ArrayList;
import java.util.List;

public class PojoPlotInstance extends PlotInstanceBase
{
	private PojoInvocableProfileInfo InvocableInfo=null;
	private PojoPlotInfo PlotInfo=null;
	private PojoPlotResourceParameterInCollection PlotInputParameters=null;
	private PojoPlotResourceParameterOutCollection PlotOutputParameters=null;
	private PojoPlanElement Element=null;
	
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
		if(!(invocable instanceof PojoInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.InvocableInfo=(PojoInvocableProfileInfo)invocable;
		this.SetName(this.InvocableInfo.ClassName);
	}

	public InvocablePlotInfo GetPlotProfile()
	{
		return this.PlotInfo;
	}

	public void SetPlotProfile(InvocablePlotInfo plot) throws WorkflowValidationException
	{
		if(!(plot instanceof PojoPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInfo=(PojoPlotInfo)plot;
	}

	public IPlotResourceInCollection GetInputParameterCollection()
	{
		return this.PlotInputParameters;
	}

	public void SetInputParameterCollection(IPlotResourceInCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof PojoPlotResourceParameterInCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInputParameters=(PojoPlotResourceParameterInCollection)parameters;
	}

	public IPlotResourceOutCollection GetOutputParameterCollection()
	{
		return this.PlotOutputParameters;
	}

	public void SetOutputParameterCollection(IPlotResourceOutCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof PojoPlotResourceParameterOutCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotOutputParameters=(PojoPlotResourceParameterOutCollection)parameters;
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
		this.Element=new PojoPlanElement();
		this.Element.SetName(this.GetName());
		this.Element.ClassName=this.InvocableInfo.ClassName;
		this.Element.ExecutionContextConfig=this.GetExecutionContext();
		this.Element.SupportsExecutionContext=(this.Element.ExecutionContextConfig != null);
		this.Element.Triggers=this.GetContingencyTriggers(this.PlotInfo);
		this.Element.Calls=this.GetCalls();
		this.PopulatePrePostElements(this.PlotInfo);
	}
	
	private List<CallBase> GetCalls() throws WorkflowProcessException
	{
		List<CallBase> calls=new ArrayList<CallBase>();
		for(PlotMethod m : this.PlotInfo.Methods)
		{
			SimpleCall c=new SimpleCall();
			calls.add(c);
			c.Order=m.Order;
			c.MethodName=this.InvocableInfo.Get(m.Signature).Name;
			c.ArgumentList=this.GetArguments(m);
			c.OutputParameter=this.GetOutputParameter(m);
		}
		return calls;
	}
	
	private IOutputParameter GetOutputParameter(PlotMethod mplot) throws WorkflowProcessException
	{
		if(mplot.IsConstructor) return null;
		if(!mplot.UseReturnValue) return null;
		Method mprof = this.InvocableInfo.Get(mplot.Signature);
		if(mprof.ReturnValue==null) throw new WorkflowProcessException("Did not find needed output parameter for method that is marked as returning plot needed value");
		PojoPlotResourceParameter param = this.PlotOutputParameters.Get(mplot.Signature, null);
		if(param==null) throw new WorkflowProcessException("Did not find needed output parameter for method that is marked as returning plot needed value");
		return (IOutputParameter)param.Parameter;
	}
	
	private List<ArgumentBase> GetArguments(PlotMethod mplot) throws WorkflowProcessException
	{
		try
		{
			List<ArgumentBase> args=new ArrayList<ArgumentBase>();
			Method mprof = this.InvocableInfo.Get(mplot.Signature);
			for(PlotParameter p : mplot.Parameters)
			{
				SimpleArgument arg=new SimpleArgument();
				args.add(arg);
				Parameter pprof=mprof.Get(p.ParameterName);
				arg.ArgumentName=p.ParameterName;
				arg.Order=pprof.Order;
				if(!p.IsFixed)
				{
					arg.Parameter=(IInputParameter)this.PlotInputParameters.Get(mplot.Order, p.ParameterName).Parameter;
				}
				else
				{
					NamedDataType ndt=this.ConstructNamedDataType(pprof.Type.EngineType,true, p.FixedValue);
					arg.Parameter= this.ConstructAndRegisterSimpleInputParameter(ndt);
				}
			}
			return args;
		}catch(ExecutionValidationException ex)
		{
			throw new WorkflowProcessException("Could not retreive argument",ex);
		}
	}
	
	private SimpleExecutionContextConfig GetExecutionContext() throws WorkflowEnvironmentException
	{
		if(!this.InvocableInfo.ExecutionContext.Supported) return null;
		SimpleExecutionContextConfig cc=new SimpleExecutionContextConfig();
		cc.KeepContextAlive=this.InvocableInfo.ExecutionContext.KeepAlive;
		if(this.InvocableInfo.ExecutionContext.ProxygRS.SupplyProxy)
		{
			cc.ProxyType=this.GetContextProxyType();
			if(cc.ProxyType.equals(ContextProxyType.None)) throw new WorkflowEnvironmentException("Retrieved unrecognized proxy type");
		}
		return cc;
	}
}
