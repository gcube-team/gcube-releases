package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CounterPlanCondition implements IPlanCondition
{
	private static Logger logger=LoggerFactory.getLogger(CounterPlanCondition.class);
	public IInputParameter CounterStartParameter;
	public IInputParameter CounterEndParameter;
	public IInputOutputParameter CurrentValueParameter;
	public IInputParameter IncrementStepParameter;
	public boolean RightBorderInclusive = false;
	
	private boolean FirstIteration=true;

	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		this.SetCurrentToStartOrIncrement(Handle);
		Double end = this.GetEndValue(Handle);
		Double current = this.GetCurrentValue(Handle);
		if (current < end) return true;
		if (current == end && this.RightBorderInclusive) return true;
		return false;
	}
	
	public void InitializeCondition()
	{
		this.FirstIteration=true;
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		this.Validate();
		logger.debug("There are "+ExcludeAvailableConstraint.size()+" elements in the exclude set");
		this.CounterStartParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.CounterEndParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.CurrentValueParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.IncrementStepParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	private void SetCurrentToStartOrIncrement(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			if(this.FirstIteration)
			{
				Double sv = this.GetStartValue(Handle);
				this.CurrentValueParameter.SetParameterValue(Handle, sv);
			}
			else
			{
				Double increment = this.GetIncrementValue(Handle);
				Double current=this.GetCurrentValue(Handle);
				this.CurrentValueParameter.SetParameterValue(Handle, current + increment);

			}
			this.FirstIteration=false;
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not Set initial current value");
		}
	}

	private Double GetStartValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.CounterStartParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private Double GetEndValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.CounterEndParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private Double GetCurrentValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.CurrentValueParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private Double GetIncrementValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.IncrementStepParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
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
			throw new ExecutionSerializationException("Could not deserialize provided counter plan consition", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetCondtionType())) throw new ExecutionSerializationException("not valid serialization of counter plan condition");
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "start");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			Element param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CounterStartParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "end");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CounterEndParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "current");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CurrentValueParameter = (IInputOutputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "increment");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.IncrementStepParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			this.RightBorderInclusive = Boolean.parseBoolean(XMLUtils.GetAttribute(XMLUtils.GetChildElementWithName(XML, "rightinclusive"), "value"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize counter plan condition", ex);
		}
	}

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.Counter;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<start>");
		buf.append(this.CounterStartParameter.ToXML());
		buf.append("</start>");
		buf.append("<end>");
		buf.append(this.CounterEndParameter.ToXML());
		buf.append("</end>");
		buf.append("<current>");
		buf.append(this.CurrentValueParameter.ToXML());
		buf.append("</current>");
		buf.append("<increment>");
		buf.append(this.IncrementStepParameter.ToXML());
		buf.append("</increment>");
		buf.append("<rightinclusive value=\""+this.RightBorderInclusive+"\"/>");
		buf.append("</condition>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.CounterStartParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.CounterEndParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.CurrentValueParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.IncrementStepParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		this.CounterStartParameter.Validate();
		this.CounterEndParameter.Validate();
		this.CurrentValueParameter.Validate();
		this.IncrementStepParameter.Validate();
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.CounterStartParameter.GetNeededVariableNames());
		vars.addAll(this.CounterEndParameter.GetNeededVariableNames());
		vars.addAll(this.CurrentValueParameter.GetNeededVariableNames());
		vars.addAll(this.IncrementStepParameter.GetNeededVariableNames());
		return vars;
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.CounterStartParameter.GetModifiedVariableNames());
		vars.addAll(this.CounterEndParameter.GetModifiedVariableNames());
		vars.addAll(this.CurrentValueParameter.GetModifiedVariableNames());
		vars.addAll(this.IncrementStepParameter.GetModifiedVariableNames());
		return vars;
	}
}
