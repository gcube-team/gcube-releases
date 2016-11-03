package gr.uoa.di.madgik.execution.plan.element.variable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SimpleInOutParameter implements IInputOutputParameter
{
	private static final long serialVersionUID = 1L;
	public String VariableName=null;

	public Object GetParameterValue(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		return Handle.GetPlan().Variables.Get(VariableName).Value.GetValue();
	}
	
	public boolean CanSuggestParameterValueType(ExecutionHandle Handle)
	{
		return Handle.GetPlan().Variables.Get(this.VariableName).Value.CanSuggestDataTypeClass();
	}
	public Class<?> SuggestParameterValueType(ExecutionHandle Handle)
	{
		return Handle.GetPlan().Variables.Get(this.VariableName).Value.GetDataTypeClass();
	}

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

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists((Element) XML, "direction") || !XMLUtils.AttributeExists((Element) XML, "process")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterDirectionType.valueOf(XMLUtils.GetAttribute((Element)XML, "direction")).equals(this.GetDirectionType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterProcessType.valueOf(XMLUtils.GetAttribute((Element)XML, "process")).equals(this.GetProcessType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element varelem=XMLUtils.GetChildElementWithName(XML, "variable");
			if(varelem==null) throw new ExecutionSerializationException("provided serialization is not valid serialization of element");
			if(!XMLUtils.AttributeExists(varelem, "name")) throw new ExecutionSerializationException("provided serialization is not valid serialization of element");
			this.VariableName=XMLUtils.GetAttribute(varelem, "name");
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public ParameterDirectionType GetDirectionType()
	{
		return ParameterDirectionType.InOut;
	}

	public ParameterProcessType GetProcessType()
	{
		return ParameterProcessType.Simple;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<param direction=\"" + this.GetDirectionType().toString()+ "\" process=\"" + this.GetProcessType().toString()+ "\">");
		buf.append("<variable name=\""+this.VariableName+"\"/>");
		buf.append("</param>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.VariableName==null || this.VariableName.trim().length()==0) throw new ExecutionValidationException("Variable name not provided");
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if(!Handle.GetPlan().Variables.Contains(VariableName)) throw new ExecutionValidationException("needed variable name not found");
		if(!Handle.GetPlan().Variables.Get(VariableName).IsAvailable && !ExcludeAvailableConstraint.contains(this.VariableName)) throw new ExecutionValidationException("needed variable not set as available");
	}

	public void SetParameterValue(ExecutionHandle Handle, Object Value) throws ExecutionRunTimeException, ExecutionValidationException
	{
		Handle.GetPlan().Variables.Update(this.VariableName, Value);
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(this.VariableName);
		return vars;
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(this.VariableName);
		return vars;
	}
}
