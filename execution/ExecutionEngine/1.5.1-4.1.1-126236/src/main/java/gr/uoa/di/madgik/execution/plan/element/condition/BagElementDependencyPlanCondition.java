package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BagElementDependencyPlanCondition implements IPlanCondition
{
	public Set<String> DependsOn=new HashSet<String>();

	public void InitializeCondition()
	{
		// Nothing to initialize
	}

	public boolean EvaluateCondition(ExecutionHandle Handle, IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		if(!(Environment instanceof BagConditionEnvironment)) throw new ExecutionRunTimeException("Incompatible environment information provided");
		for(String dep : this.DependsOn)
		{
			BagConditionalElement elem= ((BagConditionEnvironment)Environment).ElementCollection.get(dep);
			if(elem==null) throw new ExecutionRunTimeException("Dependency to node "+dep+" not found in environment");
			if(!elem.Executed) return false;
		}
		return true;
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided counter plan consition", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetCondtionType())) throw new ExecutionSerializationException("not valid serialization of counter plan condition");
			Element lstelem= XMLUtils.GetChildElementWithName(XML, "list");
			if(lstelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			List<Element> param=XMLUtils.GetChildElementsWithName(lstelem, "dep");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.DependsOn.clear();
			for(Element dep : param)
			{
				if(!XMLUtils.AttributeExists(dep, "name")) throw new ExecutionSerializationException("Provided serialization is not a valid one");
				this.DependsOn.add(XMLUtils.GetAttribute(dep, "name"));
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize counter plan condition", ex);
		}
	}

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.BagDependency;
	}

	public Set<String> GetModifiedVariableNames()
	{
		return new HashSet<String>();
	}

	public Set<String> GetNeededVariableNames()
	{
		return new HashSet<String>();
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<list>");
		for(String dep : this.DependsOn) buf.append("<dep name=\""+dep+"\"/>");
		buf.append("</list>");
		buf.append("</condition>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.DependsOn==null || this.DependsOn.size()==0) throw new ExecutionValidationException("Dependency set cannot be null or empty. Consider not seting a dependency condition");
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
	}

}
