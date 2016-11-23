package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.grs.proxy.IProxy;

public interface IExecutionContext
{
	public void Report(String Message);
	public void Report(int CurrentStep, int TotalSteps);
	public void Report(int CurrentStep, int TotalSteps,String Message);
	public void Close();
	public IProxy GetProxy();

}
