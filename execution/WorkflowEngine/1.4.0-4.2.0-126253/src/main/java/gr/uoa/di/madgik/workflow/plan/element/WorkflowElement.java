package gr.uoa.di.madgik.workflow.plan.element;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotShellParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.PojoPlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.ShellPlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.WSPlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.workflow.environment.EnvironmentCache;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plan.element.hook.HookCollection;
import gr.uoa.di.madgik.workflow.plan.element.hook.IElementHook;
import gr.uoa.di.madgik.workflow.utils.KeyUtils;

public class WorkflowElement implements IWorkflowPlanElement
{
	public String ElementName=null;
	public String PlotName=null;
	public String GroupName=null;
	public HookCollection Hooks=null;
	public EnvironmentCache Environment=null;

	public void SetElementName(String ElementName) { this.ElementName=ElementName; }

	public void SetEnvironment(EnvironmentCache Environment) { this.Environment=Environment; }

	public void SetGroupName(String GroupName) { this.GroupName=GroupName; }

	public void SetHooks(HookCollection Hooks) { this.Hooks=Hooks; }

	public void SetPlotName(String PlotName) { this.PlotName=PlotName; }

	public void Validate(EnvHintCollection Hints) throws WorkflowValidationException
	{
		try
		{
			InvocablePlotInfo plot= this.Environment.GetPlotInfo(this.PlotName,Hints);
			for(PlotLocalEnvironmentVariable var : plot.LocalEnvironment.Variables)
			{
				if(var.IsFixed)continue;
				if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Environment, IElementHook.SubType.EnvironmentVariable, KeyUtils.KeyOfEnvironmentVariable(var.Name)))
				{
					throw new WorkflowValidationException("Hook not provided for environmental variable "+var.Name);
				}
			}
			for(PlotLocalEnvironmentFile file : plot.LocalEnvironment.Files)
			{
				if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Environment, IElementHook.SubType.EnvironmentFile, KeyUtils.KeyOfEnvironmentVariable(file.Name)))
				{
					throw new WorkflowValidationException("Hook not provided for environmental file "+file.Name);
				}
			}
			if(plot instanceof ShellPlotInfo)
			{
				if(((ShellPlotInfo)plot).UseStdIn && !this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Misc, IElementHook.SubType.StdIn, KeyUtils.KeyOfStdIn()))
				{
					throw new WorkflowValidationException("Hook not provided for std in");
				}
				if(((ShellPlotInfo)plot).UseStdOut && !this.Hooks.ContainsHook(IElementHook.Direction.Out, IElementHook.Type.Misc, IElementHook.SubType.StdOut, KeyUtils.KeyOfStdOut()))
				{
					throw new WorkflowValidationException("Hook not provided for std out");
				}
				if(((ShellPlotInfo)plot).UseStdErr && !this.Hooks.ContainsHook(IElementHook.Direction.Out, IElementHook.Type.Misc, IElementHook.SubType.StdErr, KeyUtils.KeyOfStdErr()))
				{
					throw new WorkflowValidationException("Hook not provided for std err");
				}
				if(((ShellPlotInfo)plot).UseStdExit && !this.Hooks.ContainsHook(IElementHook.Direction.Out, IElementHook.Type.Misc, IElementHook.SubType.StdExit, KeyUtils.KeyOfStdExit()))
				{
					throw new WorkflowValidationException("Hook not provided for std exit");
				}
				for(PlotShellParameter par : ((ShellPlotInfo)plot).Parameters)
				{
					if(par.IsFixed) continue;
					if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Invocation, IElementHook.SubType.InvocationArgument, KeyUtils.KeyOfShellArgument(par.ParameterName)))
					{
						throw new WorkflowValidationException("Hook not provided for shell argument "+par.ParameterName);
					}
				}
			}
			else if (plot instanceof PojoPlotInfo)
			{
				for(PlotMethod meth : ((PojoPlotInfo)plot).Methods)
				{
					if(meth.UseReturnValue && !this.Hooks.ContainsHook(IElementHook.Direction.Out, IElementHook.Type.Invocation, IElementHook.SubType.InvocationReturn, KeyUtils.KeyOfInvocationReturn(meth.Order)))
					{
						throw new WorkflowValidationException("Hook not provided for invocation "+meth.Order+" return");
					}
					for(PlotParameter par : meth.Parameters)
					{
						if(par.IsFixed)continue;
						if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Invocation, IElementHook.SubType.InvocationArgument, KeyUtils.KeyOfPojoArgument(meth.Order,par.ParameterName)))
						{
							throw new WorkflowValidationException("Hook not provided for pojo argument "+par.ParameterName+" of invocation "+meth.Order);
						}
					}
				}
			}
			else if (plot instanceof WSPlotInfo)
			{
				if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Misc, IElementHook.SubType.EndPoint, KeyUtils.KeyOfEndPoint()))
				{
					throw new WorkflowValidationException("Hook not provided for ws invocation end point");
				}
				for(PlotMethod meth : ((WSPlotInfo)plot).Methods)
				{
					if(meth.UseReturnValue && !this.Hooks.ContainsHook(IElementHook.Direction.Out, IElementHook.Type.Invocation, IElementHook.SubType.InvocationReturn, KeyUtils.KeyOfInvocationReturn(meth.Order)))
					{
						throw new WorkflowValidationException("Hook not provided for invocation "+meth.Order+" return");
					}
					for(PlotParameter par : meth.Parameters)
					{
						if(par.IsFixed)continue;
						if(!this.Hooks.ContainsHook(IElementHook.Direction.In, IElementHook.Type.Invocation, IElementHook.SubType.InvocationArgument, KeyUtils.KeyOfWSArgument(meth.Order)))
						{
							throw new WorkflowValidationException("Hook not provided for ws argument of invocation "+meth.Order);
						}
					}
				}
			}
			else throw new WorkflowInternalErrorException("unrecognizable plot type");
		} catch (Exception ex)
		{
			throw new WorkflowValidationException("Could not validate element", ex);
		}
	}
}
