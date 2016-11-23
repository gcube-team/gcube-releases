package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.Set;
import org.w3c.dom.Node;

public interface IPlanCondition
{
	public enum ConditionType
	{
		Counter,
		ArrayIteration,
		DecimalRange,
		IsAvailable,
		BagDependency,
		BooleanVariable,
		Timeout
	}
	
	public void InitializeCondition();
	
	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException;

	public void Validate() throws ExecutionValidationException;
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException;
	
	public ConditionType GetCondtionType();

	public String ToXML() throws ExecutionSerializationException;
	public void FromXML(String XML) throws ExecutionSerializationException;
	public void FromXML(Node XML) throws ExecutionSerializationException;
	public Set<String> GetNeededVariableNames();
	public Set<String> GetModifiedVariableNames();
}
