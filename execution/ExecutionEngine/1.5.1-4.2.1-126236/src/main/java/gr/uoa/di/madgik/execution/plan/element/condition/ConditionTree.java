package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.ConditionUtils;

import java.io.Serializable;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConditionTree implements Serializable
{
	private static final long serialVersionUID = 1L;
	public IConditionTreeElement Root = null;

	public boolean EvaluateCondition(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		return this.EvaluateCondition(Handle,null);
	}

	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		return this.Root.EvaluateCondition(Handle,Environment);
	}

	public void InitializeCondition()
	{
		this.Root.InitializeCondition();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.Root == null) throw new ExecutionValidationException("Condition tree must have a root node defined");
		this.Root.Validate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Root.Validate();
		this.Root.ValidatePreExecution(Handle);
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<conditionTree>");
		buf.append(this.Root.ToXML());
		buf.append("</conditionTree>");
		return buf.toString();
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize Condition tree", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			Element elem = XMLUtils.GetChildElementWithName(XML, "treeElement");
			this.Root=ConditionUtils.GetConditionTreeElement(elem);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize Condition tree", ex);
		}
	}
	
	public Set<String> GetNeededVariableNames()
	{
		return this.Root.GetNeededVariableNames();
	}

	public Set<String> GetModifiedVariableNames()
	{
		return this.Root.GetModifiedVariableNames();
	}

}
