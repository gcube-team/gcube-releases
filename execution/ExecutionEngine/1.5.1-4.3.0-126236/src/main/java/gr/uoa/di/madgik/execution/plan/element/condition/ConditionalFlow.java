package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConditionalFlow
{
	public ConditionTree Condition=null;
	public IPlanElement Root=null;
	
	public void InitializeCondition()
	{
		this.Condition.InitializeCondition();
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.Condition==null) throw new ExecutionValidationException("Condition not set");
		if(this.Root==null) throw new ExecutionValidationException("Condition flow not set");
		this.Root.Validate();
		this.Condition.Validate();
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		this.Root.ValidatePreExecution(Handle);
		this.Condition.ValidatePreExecution(Handle);
	}
	
	public IPlanElement Locate(String ID)
	{
		return this.Root.Locate(ID);
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		acts.addAll(this.Root.LocateActionElements());
		return acts;
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<conditionalFlow>");
		buf.append(this.Condition.ToXML());
		buf.append(this.Root.ToXML());
		buf.append("</conditionalFlow>");
		return buf.toString();
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			Element condition = XMLUtils.GetChildElementWithName(XML, "conditionTree");
			if(condition==null) throw new ExecutionSerializationException("Not valid serialization of condition flow element");
			this.Condition=new ConditionTree();
			this.Condition.FromXML(condition);
			Element rootnode = XMLUtils.GetChildElementWithName(XML, "planElement");
			if(rootnode==null) throw new ExecutionSerializationException("Not valid serialization of condition flow element");
			this.Root=PlanElementUtils.GetPlanElement(rootnode);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize Condition Flow element", ex);
		}
	}
	
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.Root.GetNeededVariableNames());
		vars.addAll(this.Condition.GetNeededVariableNames());
		return vars;
	}
	
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars = new HashSet<String>();
		vars.addAll(this.Root.GetModifiedVariableNames());
		vars.addAll(this.Condition.GetModifiedVariableNames());
		return vars;
	}
}
