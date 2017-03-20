package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SimpleCall extends CallBase
{
	public CallType GetCallType()
	{
		return CallType.Simple;
	}

	public void Validate() throws ExecutionValidationException
	{
		super.Validate();
		for(ArgumentBase arg : this.ArgumentList) if(!(arg instanceof SimpleArgument)) throw new ExecutionValidationException("Supplied argument type is not one of the supported ones");
	}
	
	public String ToXML()throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<call type=\""+this.GetCallType().toString()+"\" order=\""+this.Order+"\">");
		buf.append("<methodName value=\""+this.MethodName+"\"/>");
		buf.append("<arguments>");
		for(ArgumentBase a : this.ArgumentList)
		{
			buf.append(a.ToXML());
		}
		buf.append("</arguments>");
		buf.append("<output>");
		if(this.OutputParameter!=null) buf.append(this.OutputParameter.ToXML());
		buf.append("</output>");
		buf.append("</call>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "order")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			Element modnode=XMLUtils.GetChildElementWithName((Element)XML, "methodName");
			if(modnode==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!XMLUtils.AttributeExists(modnode, "value")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.MethodName=XMLUtils.GetAttribute(modnode, "value");
			Element argsnode=XMLUtils.GetChildElementWithName((Element)XML, "arguments");
			if(argsnode==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.ArgumentList.clear();
			List<Element> args=XMLUtils.GetChildElementsWithName(argsnode, "argument");
			for(Element arg : args)
			{
				this.ArgumentList.add(PlanElementUtils.GetArgument(arg));
			}
			Element outputelement=XMLUtils.GetChildElementWithName(XML, "output");
			if(outputelement==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element outparam= XMLUtils.GetChildElementWithName(outputelement, "param");
			if(outparam==null) this.OutputParameter=null;
			else this.OutputParameter=(IOutputParameter)ParameterUtils.GetParameter(outparam);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
