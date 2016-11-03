package gr.uoa.di.madgik.workflow.plan.element;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.workflow.environment.EnvironmentCache;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plan.element.hook.HookCollection;

public interface IWorkflowPlanElement
{
	public void SetElementName(String ElementName);
	public void SetPlotName(String PlotName);
	public void SetGroupName(String GroupName);
	public void SetHooks(HookCollection Hooks);
	public void SetEnvironment(EnvironmentCache Environment);
	public void Validate(EnvHintCollection Hints) throws WorkflowValidationException;
}
