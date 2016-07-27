package gr.uoa.di.madgik.execution.plan.element.condition;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputOutputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ArrayIterationPlanCondition implements IPlanCondition
{
	private static Logger logger=LoggerFactory.getLogger(ArrayIterationPlanCondition.class);
	public IInputParameter ArrayParameter;
	public IInputOutputParameter CurrentValueParameter;
	public IOutputParameter CurrentArrayValueParameter;
//	public IInputParameter CounterStartParameter;
//	public IInputParameter CounterEndParameter;
//	public IInputParameter IncrementStepParameter;
//	public boolean RightBorderInclusive = false;
	
	private boolean FirstIteration=true;

	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		this.SetCurrentToStartOrIncrement(Handle);
		int end = this.GetEndValue(Handle);
		int current = this.GetCurrentValue(Handle);
		if (current < end)
		{
			this.SetCurrentArrayValue(Handle, current);
			return true;
		}
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
		this.ArrayParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.CurrentValueParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.CurrentArrayValueParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
//		this.CounterStartParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
//		this.CounterEndParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
//		this.IncrementStepParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}
	
	private void SetCurrentArrayValue(ExecutionHandle Handle, int index) throws ExecutionRunTimeException
	{
		try
		{
			this.CurrentArrayValueParameter.SetParameterValue(Handle, Array.get(this.ArrayParameter.GetParameterValue(Handle),index));
		} catch (Exception e)
		{
			throw new ExecutionRunTimeException("Unable to set current array value ofr iteration "+index, e);
		}
	}

	private void SetCurrentToStartOrIncrement(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			if(this.FirstIteration)
			{
				int sv = this.GetStartValue(Handle);
				this.CurrentValueParameter.SetParameterValue(Handle, sv);
			}
			else
			{
				int increment = this.GetIncrementValue(Handle);
				int current=this.GetCurrentValue(Handle);
				this.CurrentValueParameter.SetParameterValue(Handle, current + increment);

			}
			this.FirstIteration=false;
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not Set initial current value");
		}
	}

	private int GetStartValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return 0;
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private int GetEndValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return Array.getLength(this.ArrayParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private int GetCurrentValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsInteger(this.CurrentValueParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private int GetIncrementValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return 1;
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
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "array");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			Element param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.ArrayParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "current");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CurrentValueParameter = (IInputOutputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "value");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CurrentArrayValueParameter = (IOutputParameter)ParameterUtils.GetParameter(param);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize counter plan condition", ex);
		}
	}

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.ArrayIteration;
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<array>");
		buf.append(this.ArrayParameter.ToXML());
		buf.append("</array>");
		buf.append("<current>");
		buf.append(this.CurrentValueParameter.ToXML());
		buf.append("</current>");
		buf.append("<value>");
		buf.append(this.CurrentArrayValueParameter.ToXML());
		buf.append("</value>");
		buf.append("</condition>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.CurrentArrayValueParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.CurrentValueParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.ArrayParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		this.CurrentArrayValueParameter.Validate();
		this.CurrentValueParameter.Validate();
		this.ArrayParameter.Validate();
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.CurrentArrayValueParameter.GetNeededVariableNames());
		vars.addAll(this.CurrentValueParameter.GetNeededVariableNames());
		vars.addAll(this.ArrayParameter.GetNeededVariableNames());
		return vars;
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.CurrentArrayValueParameter.GetModifiedVariableNames());
		vars.addAll(this.CurrentValueParameter.GetModifiedVariableNames());
		vars.addAll(this.ArrayParameter.GetModifiedVariableNames());
		return vars;
	}
}
