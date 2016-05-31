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

public class DecimalRangePlanCondition implements IPlanCondition
{
	private static Logger logger=LoggerFactory.getLogger(DecimalRangePlanCondition.class);
	public static double DefaultEpsilon=0.00001;
	public IInputParameter RangeStartParameter;
	public IInputParameter RangeEndParameter;
	public IInputOutputParameter CurrentValueParameter;
	public Boolean LeftBorderInclusive = false;
	public Boolean RightBorderInclusive = false;
	public double Epsilon=DecimalRangePlanCondition.DefaultEpsilon;

	public ConditionType GetCondtionType()
	{
		return IPlanCondition.ConditionType.DecimalRange;
	}

	public void InitializeCondition()
	{
	}
	
	public boolean EvaluateCondition(ExecutionHandle Handle,IConditionEnvironment Environment) throws ExecutionRunTimeException
	{
		Double start=this.GetStartValue(Handle);
		Double end=this.GetEndValue(Handle);
		Double current=this.GetCurrentValue(Handle);
		logger.debug("Evaluating Range condition with start inclusive("+this.LeftBorderInclusive+") = "+start+" end inclusive("+this.RightBorderInclusive+") = "+end+" current = "+current);
		if(this.FuzzyLess(current, start)) return false;
		if(this.FuzzyEqual(current, start) && !this.LeftBorderInclusive) return false;
		if(this.FuzzyGreater(current, end)) return false;
		if(this.FuzzyEqual(current, end) && !this.RightBorderInclusive) return false;
		return true;
	}
	
	private boolean FuzzyEqual(Double left, Double right)
	{
		if (Math.abs(left - right) < this.Epsilon) return true;
		return false;
	}
	
	private boolean FuzzyLess(Double left, Double right)
	{
		double leftSignum= Math.signum(left);
		double rightSignum= Math.signum(right);
		if(leftSignum<rightSignum) return true;
		else if (leftSignum>rightSignum) return false;
		else if (this.FuzzyEqual(leftSignum, (double)0)) return false;
		else if (leftSignum>0)
		{
			Double diffSignum=Math.signum(left-right);
			if(this.FuzzyEqual(diffSignum, (double)0)) return false;
			if(this.FuzzyEqual(diffSignum, (double)-1)) return true;
			if(this.FuzzyEqual(diffSignum, (double)1)) return false;
		}
		else if(leftSignum<0)
		{
			Double diffSignum=Math.signum(Math.abs(left)-Math.abs(right));
			if(this.FuzzyEqual(diffSignum, (double)0)) return false;
			if(this.FuzzyEqual(diffSignum, (double)-1)) return false;
			if(this.FuzzyEqual(diffSignum, (double)1)) return true;
		}
		return false;
	}
	
	private boolean FuzzyGreater(Double left, Double right)
	{
		double leftSignum= Math.signum(left);
		double rightSignum= Math.signum(right);
		if(leftSignum>rightSignum) return true;
		else if (leftSignum<rightSignum) return false;
		else if (this.FuzzyEqual(leftSignum, (double)0)) return false;
		else if (leftSignum>0)
		{
			Double diffSignum=Math.signum(left-right);
			if(this.FuzzyEqual(diffSignum, (double)0)) return false;
			if(this.FuzzyEqual(diffSignum, (double)-1)) return false;
			if(this.FuzzyEqual(diffSignum, (double)1)) return true;
		}
		else if(leftSignum<0)
		{
			Double diffSignum=Math.signum(Math.abs(left)-Math.abs(right));
			if(this.FuzzyEqual(diffSignum, (double)0)) return false;
			if(this.FuzzyEqual(diffSignum, (double)-1)) return true;
			if(this.FuzzyEqual(diffSignum, (double)1)) return false;
		}
		return false;
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		this.Validate();
		this.RangeEndParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.RangeStartParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.CurrentValueParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	private Double GetStartValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.RangeStartParameter.GetParameterValue(Handle));
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not retrieve needed value", ex);
		}
	}

	private Double GetEndValue(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			return DataTypeUtils.GetValueAsDouble(this.RangeEndParameter.GetParameterValue(Handle));
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
			Element tmpelem= XMLUtils.GetChildElementWithName(XML, "start");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			Element param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.RangeStartParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "end");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.RangeEndParameter = (IInputParameter)ParameterUtils.GetParameter(param);
			tmpelem= XMLUtils.GetChildElementWithName(XML, "current");
			if(tmpelem==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			param=XMLUtils.GetChildElementWithName(tmpelem, "param");
			if(param==null) throw new ExecutionSerializationException("Provided serialization is not a valid one");
			this.CurrentValueParameter = (IInputOutputParameter)ParameterUtils.GetParameter(param);
			this.LeftBorderInclusive = Boolean.parseBoolean(XMLUtils.GetAttribute(XMLUtils.GetChildElementWithName(XML, "leftinclusive"), "value"));
			this.RightBorderInclusive = Boolean.parseBoolean(XMLUtils.GetAttribute(XMLUtils.GetChildElementWithName(XML, "rightinclusive"), "value"));
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize range plan condition", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<condition type=\"" + this.GetCondtionType().toString() + "\">");
		buf.append("<start>");
		buf.append(this.RangeStartParameter.ToXML());
		buf.append("</start>");
		buf.append("<end>");
		buf.append(this.RangeEndParameter.ToXML());
		buf.append("</end>");
		buf.append("<current>");
		buf.append(this.CurrentValueParameter.ToXML());
		buf.append("</current>");
		buf.append("<leftinclusive value=\"" + this.LeftBorderInclusive.toString() + "\"/>");
		buf.append("<rightinclusive value=\"" + this.RightBorderInclusive.toString() + "\"/>");
		buf.append("</condition>");
		return buf.toString();
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.RangeStartParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.RangeEndParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		if (this.CurrentValueParameter == null) { throw new ExecutionValidationException("Needed variable name has not been defined"); }
		this.RangeStartParameter.Validate();
		this.RangeEndParameter.Validate();
		this.CurrentValueParameter.Validate();
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> inputs=new HashSet<String>();
		inputs.addAll(this.RangeStartParameter.GetNeededVariableNames());
		inputs.addAll(this.RangeEndParameter.GetNeededVariableNames());
		inputs.addAll(this.CurrentValueParameter.GetNeededVariableNames());
		return inputs;
	}

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> inputs=new HashSet<String>();
		inputs.addAll(this.RangeStartParameter.GetModifiedVariableNames());
		inputs.addAll(this.RangeEndParameter.GetModifiedVariableNames());
		inputs.addAll(this.CurrentValueParameter.GetModifiedVariableNames());
		return inputs;
	}

}
