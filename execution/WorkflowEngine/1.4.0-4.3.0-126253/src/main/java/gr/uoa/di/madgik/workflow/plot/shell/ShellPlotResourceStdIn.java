package gr.uoa.di.madgik.workflow.plot.shell;

import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;

public class ShellPlotResourceStdIn implements IPlotResource
{
	public IInputParameter Input=null;
	public boolean IsFile=false;
}
