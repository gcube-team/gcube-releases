package gr.uoa.di.madgik.workflow.plot.commons;

import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;

public class PlotResourceEnvironmentFile implements IPlotResource
{
	public String FileName;
//	public NamedDataType Value;
	public IInputParameter InParameter;
	public IOutputParameter OutParameter;
}
