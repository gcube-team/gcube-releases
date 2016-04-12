package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

import java.io.Serializable;
import java.util.Set;
import org.w3c.dom.Node;

public interface IExternalFilter extends Serializable
{
	public Set<String> GetInputVariableNames();
	public Set<String> GetStoreOutputVariableName();
	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException;
	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException;
	public boolean StoreOutput();
	public boolean SupportsOnLineFiltering();
	public void Validate() throws ExecutionValidationException;
	public void ValidateForOnlineFiltering() throws ExecutionValidationException;
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException;
	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException;
	public String ToXML() throws ExecutionSerializationException;
	public void FromXML(Node XML) throws ExecutionSerializationException;
}
