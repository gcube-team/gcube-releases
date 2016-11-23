package gr.uoa.di.madgik.execution.plan.element.variable;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

import java.io.Serializable;
import java.util.Set;
import org.w3c.dom.Node;

public interface IParameter extends Serializable
{
	public enum ParameterDirectionType
	{
		In,
		Out,
		InOut
	}
	
	public enum ParameterProcessType
	{
		Filter,
		Simple
	}
	
	public boolean CanSuggestParameterValueType(ExecutionHandle Handle);
	public Class<?> SuggestParameterValueType(ExecutionHandle Handle);
	
	public void Validate()throws ExecutionValidationException;
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint)  throws ExecutionValidationException;

	public Set<String> GetModifiedVariableNames();
	public Set<String> GetNeededVariableNames();

	public String ToXML()throws ExecutionSerializationException;
	public void FromXML(String XML) throws ExecutionSerializationException;
	public void FromXML(Node XML) throws ExecutionSerializationException;
	
	public IParameter.ParameterDirectionType GetDirectionType();
	public IParameter.ParameterProcessType GetProcessType();
}
