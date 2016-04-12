package gr.uoa.di.madgik.workflow.plot.ws;

import gr.uoa.di.madgik.commons.channel.proxy.local.LocalNozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.Method;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.WSPlotInfo;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WSSOAPPlanElement;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterSerializationFilter;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase.ContextProxyType;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPCall;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowProcessException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.PlotInstanceBase;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import java.util.ArrayList;
import java.util.List;

public class WSPlotInstance extends PlotInstanceBase
{
	private WSInvocableProfileInfo InvocableInfo=null;
	private WSPlotInfo PlotInfo=null;
	private WSPlotResourceParameterInCollection PlotInputParameters=null;
	private WSPlotResourceParameterOutCollection PlotOutputParameters=null;
	private WSSOAPPlanElement Element=null;
	
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
		if(!(invocable instanceof WSInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.InvocableInfo=(WSInvocableProfileInfo)invocable;
		this.SetName(this.InvocableInfo.ClassName);
	}

	public InvocablePlotInfo GetPlotProfile()
	{
		return this.PlotInfo;
	}

	public void SetPlotProfile(InvocablePlotInfo plot) throws WorkflowValidationException
	{
		if(!(plot instanceof WSPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInfo=(WSPlotInfo)plot;
	}

	public IPlotResourceInCollection GetInputParameterCollection()
	{
		return this.PlotInputParameters;
	}

	public void SetInputParameterCollection(IPlotResourceInCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof WSPlotResourceParameterInCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotInputParameters=(WSPlotResourceParameterInCollection)parameters;
	}

	public IPlotResourceOutCollection GetOutputParameterCollection()
	{
		return this.PlotOutputParameters;
	}

	public void SetOutputParameterCollection(IPlotResourceOutCollection parameters) throws WorkflowValidationException
	{
		if(!(parameters instanceof WSPlotResourceParameterOutCollection)) throw new WorkflowValidationException("Incompatible type provided");
		this.PlotOutputParameters=(WSPlotResourceParameterOutCollection)parameters;
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
		this.Element=new WSSOAPPlanElement();
		this.Element.SetName(this.GetName());
		this.Element.ServiceEndPoint=this.PlotInputParameters.GetEndPoint().ServiceEndpoint;
		this.Element.ExecutionContextConfig=this.GetExecutionContext();
		this.Element.SupportsExecutionContext=(this.Element.ExecutionContextConfig != null);
		this.Element.Triggers=this.GetContingencyTriggers(this.PlotInfo);
		this.Element.Calls=this.GetCalls(this.Element.SupportsExecutionContext);
		this.PopulatePrePostElements(this.PlotInfo);
	}
	
	private List<CallBase> GetCalls(boolean SupportsContext) throws WorkflowProcessException
	{
		List<CallBase> calls=new ArrayList<CallBase>();
		for(PlotMethod m : this.PlotInfo.Methods)
		{
			WSSOAPCall c=new WSSOAPCall();
			calls.add(c);
			c.Order=m.Order;
			c.MethodName=this.InvocableInfo.Get(m.Signature).Name;
			c.ActionURN=this.InvocableInfo.Get(m.Signature).MethodURN;
			c.ArgumentList.add(this.GetArgument(m));
			c.OutputParameter=this.GetOutputParameter(m);
			if(SupportsContext)
			{
				c.ExecutionContextToken=this.InvocableInfo.Get(m.Signature).ExecutioContextToken;
				if(c.ExecutionContextToken==null || c.ExecutionContextToken.trim().length()==0) throw new WorkflowProcessException("Decllared needed execution context but no token provided");
				c.PostCreationFilters.add(new ParameterSerializationFilter());
				c.PostCreationFilters.get(0).Order=0;
			}
		}
		return calls;
	}
	
	private WSSOAPArgument GetArgument(PlotMethod mplot) throws WorkflowProcessException
	{
		WSSOAPArgument arg=new WSSOAPArgument();
		arg.ArgumentName="full envelope";
		arg.Order=0;
		arg.Parameter=this.PlotInputParameters.GetMethodInput(mplot.Order).Input;
		return arg;
	}
	
	private IOutputParameter GetOutputParameter(PlotMethod mplot) throws WorkflowProcessException
	{
		if(mplot.IsConstructor) return null;
		if(!mplot.UseReturnValue) return null;
		Method mprof = this.InvocableInfo.Get(mplot.Signature);
		if(mprof.ReturnValue==null) throw new WorkflowProcessException("Did not find needed output parameter for method that is marked as returning plot needed value");
		WSPlotResourceMethodOutput param = this.PlotOutputParameters.Get(mplot.Order);
		if(param==null) throw new WorkflowProcessException("Did not find needed output parameter for method that is marked as returning plot needed value");
		return (IOutputParameter)param.Output;
	}
	
	private WSExecutionContextConfig GetExecutionContext() throws WorkflowEnvironmentException
	{
		if(!this.InvocableInfo.ExecutionContext.Supported) return null;
		WSExecutionContextConfig cc=new WSExecutionContextConfig();
		cc.KeepContextAlive=this.InvocableInfo.ExecutionContext.KeepAlive;
		if(this.InvocableInfo.ExecutionContext.ProxygRS.SupplyProxy)
		{
			cc.ProxyType=this.GetContextProxyType();
			if(cc.ProxyType.equals(ContextProxyType.None)) throw new WorkflowEnvironmentException("Retrieved unrecognized proxy type");
		}
		if(this.InvocableInfo.ExecutionContext.ReportsProgress)
		{
			switch(this.InvocableInfo.ExecutionContext.ProgressProvider)
			{
				case Local:
				{
					cc.NozzleConfig=new LocalNozzleConfig(false,0);
					break;
				}
				case TCP:
				{
					cc.NozzleConfig=new TCPServerNozzleConfig(false, 0);
				break;
				}
				default:
				{
					throw new WorkflowEnvironmentException("Retrieved unrecognized progress provider type");
				}
			}
		}
		return cc;
	}
}
