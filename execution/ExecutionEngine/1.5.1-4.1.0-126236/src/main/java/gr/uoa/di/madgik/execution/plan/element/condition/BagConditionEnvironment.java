package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.execution.utils.BackgroundExecution;
import java.util.List;
import java.util.Map;

public class BagConditionEnvironment implements IConditionEnvironment
{
	public Map<String,BagConditionalElement> ElementCollection=null;
	public boolean ProgressDoneInIteration=false;
	public List<BackgroundExecution> workers=null;
}
