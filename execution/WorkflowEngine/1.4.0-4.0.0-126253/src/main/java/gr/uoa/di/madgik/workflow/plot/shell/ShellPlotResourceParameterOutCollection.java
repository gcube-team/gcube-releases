package gr.uoa.di.madgik.workflow.plot.shell;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.ShellInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.ShellPlotInfo;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;

public class ShellPlotResourceParameterOutCollection implements IPlotResourceOutCollection
{
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	public ShellPlotResourceStdOut StdOutParameter=null;
	public ShellPlotResourceStdErr StdErrParameter=null;
	public ShellPlotResourceStdExit StdExitParameter=null;

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
		if(param instanceof ShellPlotResourceStdOut) this.StdOutParameter=(ShellPlotResourceStdOut)param;
		else if(param instanceof ShellPlotResourceStdErr) this.StdErrParameter=(ShellPlotResourceStdErr)param;
		else if(param instanceof ShellPlotResourceStdExit) this.StdExitParameter=(ShellPlotResourceStdExit)param;
		else throw new WorkflowValidationException("unrecognized type of resource provided");
	}
	
	public ShellPlotResourceStdOut GetStdOut()
	{
		return this.StdOutParameter;
	}
	
	public ShellPlotResourceStdErr GetStdErr()
	{
		return this.StdErrParameter;
	}
	
	public ShellPlotResourceStdExit GetStdExit()
	{
		return this.StdExitParameter;
	}

	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof ShellPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof ShellInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(((ShellPlotInfo)this.plotInfo).UseStdOut && (this.StdOutParameter==null || this.StdOutParameter.Output==null)) throw new WorkflowValidationException("Needed output parameter not set");
		if(((ShellPlotInfo)this.plotInfo).UseStdErr && (this.StdErrParameter==null || this.StdErrParameter.Output==null)) throw new WorkflowValidationException("Needed output parameter not set");
		if(((ShellPlotInfo)this.plotInfo).UseStdExit && (this.StdExitParameter==null || this.StdExitParameter.Output==null)) throw new WorkflowValidationException("Needed output parameter not set");
	}

}
