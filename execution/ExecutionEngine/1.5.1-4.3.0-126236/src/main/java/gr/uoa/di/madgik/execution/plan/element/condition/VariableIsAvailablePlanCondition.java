package gr.uoa.di.madgik.execution.plan.element.condition;

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

public class VariableIsAvailablePlanCondition implements IPlanCondition
{	
	public String VariableToCheck=null;

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.IsAvailable;
	}

	public void InitializeCondition()
	{
	}

	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		return Handle.GetPlan().Variables.Get(this.VariableToCheck).IsAvailable;
	}

	public Set<String> GetModifiedVariableNames()
	{
		return new HashSet<String>();
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.add(this.VariableToCheck);
		return vars;
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.VariableToCheck==null) throw new ExecutionValidationException("Needed variable name has not been defined");
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		if(!Handle.GetPlan().Variables.Contains(this.VariableToCheck)) throw new ExecutionValidationException("Needed variable is not present");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<var name=\""+this.VariableToCheck+"\"/>");
		buf.append("</condition>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetCondtionType())) throw new ExecutionSerializationException("not valid serialization of range plan condition");
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "var");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.VariableToCheck=XMLUtils.GetChildText(tmpelem);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize range plan condition", ex);
		}
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

}
