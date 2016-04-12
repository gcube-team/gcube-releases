package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BagConditionalElement
{
	public IPlanElement Element=null;
	public ConditionTree Condition=null;
	public boolean Executed=false;
	public boolean PickedForExecution=false;

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			Element member = XMLUtils.GetChildElementWithName(XML, "planElement");
			if(member==null) throw new ExecutionSerializationException("Not valid serialization");
			this.Element=PlanElementUtils.GetPlanElement(member);
			Element conditionnode = XMLUtils.GetChildElementWithName(XML, "conditionTree");
			if(conditionnode!=null)
			{
				this.Condition = new ConditionTree();
				this.Condition.FromXML(conditionnode);
			}
			else this.Condition=null;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<bagElement>");
		buf.append(this.Element.ToXML());
		if(this.Condition!=null) buf.append(this.Condition.ToXML());
		buf.append("</bagElement>");
		return buf.toString();
	}
}
