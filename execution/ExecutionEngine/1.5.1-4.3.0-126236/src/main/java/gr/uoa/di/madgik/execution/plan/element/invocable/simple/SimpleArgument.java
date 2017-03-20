package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SimpleArgument extends ArgumentBase
{
	public ArgumentType GetArgumentType()
	{
		return ArgumentType.Simple;
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<argument type=\""+this.GetArgumentType().toString()+"\" order=\"" + this.Order + "\" name=\"" + this.ArgumentName + "\">");
		buf.append(this.Parameter.ToXML());
		buf.append("</argument>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists((Element) XML, "order") || !XMLUtils.AttributeExists((Element) XML, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order = Integer.parseInt(XMLUtils.GetAttribute((Element) XML, "order"));
			this.ArgumentName = XMLUtils.GetAttribute((Element) XML, "name");
			Element paramelem=XMLUtils.GetChildElementWithName(XML, "param");
			if(paramelem==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Parameter=(IInputParameter)ParameterUtils.GetParameter(paramelem);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
