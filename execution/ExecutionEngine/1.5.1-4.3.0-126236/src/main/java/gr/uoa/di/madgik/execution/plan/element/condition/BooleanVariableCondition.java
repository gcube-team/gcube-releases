package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BooleanVariableCondition implements IPlanCondition
{
	public IInputParameter FlagParameter=null;;

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.BooleanVariable;
	}

	public void InitializeCondition()
	{
	}

	public boolean EvaluateCondition(ExecutionHandle Handle, IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsBoolean(this.FlagParameter.GetParameterValue(Handle));
		} catch (ExecutionValidationException ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.FlagParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		this.FlagParameter.Validate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		this.Validate();
		this.FlagParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> inputs=new HashSet<String>();
		inputs.addAll(this.FlagParameter.GetModifiedVariableNames());
		return inputs;
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> inputs=new HashSet<String>();
		inputs.addAll(this.FlagParameter.GetNeededVariableNames());
		return inputs;
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided Range plan consition", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetCondtionType())) throw new ExecutionSerializationException("not valid serialization of range plan condition");
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "flag");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			Element param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.FlagParameter = (IInputParameter)ParameterUtils.GetParameter(param);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize range plan condition", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<flag>");
		buf.append(this.FlagParameter.ToXML());
		buf.append("</flag>");
		buf.append("</condition>");
		return buf.toString();
	}
	
}
