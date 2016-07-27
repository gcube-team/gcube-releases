package gr.uoa.di.madgik.execution.plan.element.invocable;

public interface IExecutionContextEnabled
{
	public void SetExecutionContext(IExecutionContext Context);
	public IExecutionContext GetExecutionContext();
}
