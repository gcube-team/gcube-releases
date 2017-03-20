package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TimeOutPlanCondition implements IPlanCondition
{
	public long TimeoutThreshold=Long.MAX_VALUE;
	public long StartTime=0;

	public ConditionType GetCondtionType()
	{
		return ConditionType.Timeout;
	}

	public void InitializeCondition()
	{
		this.StartTime=Calendar.getInstance().getTimeInMillis();
	}

	public Set<String> GetModifiedVariableNames()
	{
		return new HashSet<String>();
	}

	public Set<String> GetNeededVariableNames()
	{
		return new HashSet<String>();
	}

	public void Validate() throws ExecutionValidationException
	{
		if(this.TimeoutThreshold==Long.MIN_VALUE || this.TimeoutThreshold==Long.MAX_VALUE) throw new ExecutionValidationException("Timeout threshold is not properly defined");
		if(this.TimeoutThreshold<=0) throw new ExecutionValidationException("Timeout threshold is not properly defined");
	}

	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
	}

	public boolean EvaluateCondition(ExecutionHandle Handle, IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		long span=Calendar.getInstance().getTimeInMillis() - this.StartTime;
		if(span<this.TimeoutThreshold) return true;
		return false;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<threshold value=\""+this.TimeoutThreshold+"\"/>");
		buf.append("</condition>");
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
			throw new ExecutionSerializationException("Could not deserialize provided counter plan consition", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetCondtionType())) throw new ExecutionSerializationException("not valid serialization of counter plan condition");
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "threshold");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			if(!XMLUtils.AttributeExists(tmpelem, "value")) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.TimeoutThreshold=Long.parseLong(XMLUtils.GetAttribute(tmpelem, "value"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize counter plan condition", ex);
		}
	}
}
