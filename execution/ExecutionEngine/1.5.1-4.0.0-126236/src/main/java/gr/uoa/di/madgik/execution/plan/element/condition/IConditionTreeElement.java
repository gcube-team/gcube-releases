package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

import java.io.Serializable;
import java.util.Set;
import org.w3c.dom.Element;

public interface IConditionTreeElement extends Serializable
{
	public enum TreeElementType
	{
		Node,
		Leaf
	}
	
	public TreeElementType GetTreeElementType();
	public void Validate() throws ExecutionValidationException;
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException;
	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException;
	public String ToXML() throws ExecutionSerializationException;
	public void FromXML(String XML) throws ExecutionSerializationException;
	public void FromXML(Element XML) throws ExecutionSerializationException;
	public Set<String> GetNeededVariableNames();
	public Set<String> GetModifiedVariableNames();
	public void InitializeCondition();
}
