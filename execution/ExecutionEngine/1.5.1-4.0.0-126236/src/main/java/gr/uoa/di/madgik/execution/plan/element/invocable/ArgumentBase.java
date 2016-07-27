package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;

import java.io.Serializable;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class ArgumentBase implements Comparable<ArgumentBase>,Serializable
{
	public enum ArgumentType
	{
		Simple,
		WSSOAP,
		WSREST
	}
	
	public int Order = 0;
	public String ArgumentName;
	public IInputParameter Parameter = null;
	private transient Object Value = null;

	public void Validate() throws ExecutionValidationException
	{
		if (this.ArgumentName == null || this.ArgumentName.trim().length() == 0) throw new ExecutionValidationException("Argument name not provided");
		if (this.Parameter == null) throw new ExecutionValidationException("No parameter provided");
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		this.Parameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	public int compareTo(ArgumentBase o)
	{
		return Integer.valueOf(this.Order).compareTo(o.Order);
	}

	public void EvaluateArgument(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		this.Value=this.Parameter.GetParameterValue(Handle);
	}

	public Class<?> GetValueClass(ExecutionHandle Handle)
	{
		if(this.Parameter.CanSuggestParameterValueType(Handle)) return this.Parameter.SuggestParameterValueType(Handle);
		return this.Value.getClass();
	}

	public Object GetValue()
	{
		return this.Value;
	}

	public abstract String ToXML() throws ExecutionSerializationException;

	public abstract void FromXML(Node XML) throws ExecutionSerializationException;
	
	public abstract ArgumentType GetArgumentType();

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	
	public Set<String> GetModifiedVariableNames()
	{
		return this.Parameter.GetModifiedVariableNames();
	}

	public Set<String> GetNeededVariableNames()
	{
		return this.Parameter.GetNeededVariableNames();
	}
}
