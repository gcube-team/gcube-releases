package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.ConditionUtils;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConditionTreeLeaf implements IConditionTreeElement
{
	public IPlanCondition Condition = null;

	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		return this.Condition.EvaluateCondition(Handle,Environment);
	}

	public void InitializeCondition()
	{
		this.Condition.InitializeCondition();
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided serialization of condition tree leaf", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree leaf");
			if(!IConditionTreeElement.TreeElementType.valueOf(XMLUtils.GetAttribute(XML, "type")).equals(this.GetTreeElementType())) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree leaf");
			Element elem = XMLUtils.GetChildElementWithName(XML, "condition");
			if(elem==null) throw new ExecutionSerializationException("Provided serialization is not a valid serialization of a condition tree leaf");
			this.Condition=ConditionUtils.GetPlanCondition(elem);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided serialization of condition tree leaf", ex);
		}
	}

	public TreeElementType GetTreeElementType()
	{
		return TreeElementType.Leaf;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<treeElement type=\"" + this.GetTreeElementType().toString() + "\">");
		buf.append(this.Condition.ToXML());
		buf.append("</treeElement>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.Condition == null) throw new ExecutionValidationException("Condition tree leaf doesn't have a condition set");
		this.Condition.Validate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Condition.Validate();
		this.Condition.ValidatePreExecution(Handle);
	}

	public Set<String> GetNeededVariableNames()
	{
		return this.Condition.GetNeededVariableNames();
	}

	public Set<String> GetModifiedVariableNames()
	{
		return this.Condition.GetModifiedVariableNames();
	}
}
